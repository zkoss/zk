// eslint-disable-next-line no-undef
module.exports = function ({types: t}) {

	function createNestedMemberExpression(identifiers) {
		if (identifiers.length === 1) {
			return t.identifier(identifiers[0]);
		} else {
			const [head, ...tail] = identifiers;
			return t.memberExpression(
				createNestedMemberExpression(tail),
				t.identifier(head)
			);
		}
	}

	return {
		visitor: {
			Program: {
				exit(path) {
					let dir = this.file.opts.filename.replace(/-/g, '_').split('/');
					const jsLoc = dir.findIndex(x => x === 'js'),
						file = dir[dir.length - 1],
						privateVars = new Set(),
						privateFuncs = new Set(),
						funcCallCount = new Map();

					// pass if [not in js folder] or [not ts file] or [is global.d.ts] or [is index.ts]
					if (jsLoc === -1 || !file.endsWith('ts') || file === 'global.d.ts' || file === 'index.ts') return;

					// simplify whole dir
					dir = dir.slice(jsLoc + 1);
					dir[dir.length - 1] = file.replace('.ts', '');

					// sort order for follow-up
					dir.unshift('window');
					dir.push('_');
					dir.reverse();

					// visit all nodes
					path.node.body.forEach((node, index) => {
						// collect private variables
						if (t.isVariableDeclaration(node)) {
							node.declarations.forEach((declaration) => {
								if (t.isIdentifier(declaration.id))
									privateVars.add(declaration.id.name);
							});
						// collect private functions and replace them with `window.PACKAGE._._func = function (args) {...}`
						} else if (t.isFunctionDeclaration(node) && t.isIdentifier(node.id)) {
							// if (node.id.name === '_zk') return;
							privateFuncs.add(node.id.name);
							funcCallCount.set(node.id.name, 0);
							path.get('body')[index].replaceWith(
								t.expressionStatement(
									t.assignmentExpression(
										'=',
										createNestedMemberExpression([node.id.name, ...dir]),
										t.functionExpression(
											undefined,
											node.params,
											node.body,
											node.generator || false,
											node.async || false
										)
									)
								)
							);
						}
					});

					// insert check-exist if statements in the start of the file
					for (let i = 0; i < dir.length - 1; i++) {
						const nestedExpression = createNestedMemberExpression(dir.slice(i));
						path.unshiftContainer('body',
							t.ifStatement(
								t.unaryExpression('!', nestedExpression),
								t.expressionStatement(
									t.assignmentExpression(
										'=',
										nestedExpression,
										t.objectExpression([])
									)
								)
							)
						);
					}

					// append check-exist if statements in the end of the file
					for (let i = dir.length - 2; i >= 0; i--) {
						const nestedExpression = createNestedMemberExpression(dir.slice(i));
						path.pushContainer('body',
							t.ifStatement(
								t.unaryExpression('!', nestedExpression),
								t.expressionStatement(
									t.assignmentExpression(
										'=',
										nestedExpression,
										t.objectExpression([])
									)
								)
							)
						);
					}

					// export private variable to `window.PACKAGE._._var = _var`
					privateVars.forEach(v => {
						path.pushContainer('body',
							t.expressionStatement(
								t.assignmentExpression(
									'=',
									t.memberExpression(
										createNestedMemberExpression(dir),
										t.identifier(v)
									),
									t.identifier(v)
								)
							)
						);
					});

					function assExp(assignPath) {
						dfs(assignPath.get('left'));
						dfs(assignPath.get('right'));

						const left = assignPath.node.left,
							right = assignPath.node.right;
						// case: FUNC = x -> window.PACKAGE._.FUNC = x
						if (t.isIdentifier(left) && privateFuncs.has(left.name)) {
							funcCallCount.set(left.name, funcCallCount.get(left.name) + 1);
							assignPath.node.left = createNestedMemberExpression([left.name, ...dir]);
						}
						// case: x = FUNC -> x = window.PACKAGE._.FUNC
						if (t.isIdentifier(right) && privateFuncs.has(right.name)) {
							funcCallCount.set(right.name, funcCallCount.get(right.name) + 1);
							assignPath.node.right = createNestedMemberExpression([right.name, ...dir]);
						}
					}

					function memExp(memPath) {
						const object = memPath.node.object;
						// case: FUNC.x = x -> window.PACKAGE._.FUNC.x = x
						if (t.isIdentifier(object) && privateFuncs.has(object.name)) {
							funcCallCount.set(object.name, funcCallCount.get(object.name) + 1);
							memPath.get('object').replaceWith(createNestedMemberExpression([object.name, ...dir]));
						}
					}

					function condExp(condPath) {
						const { consequent, alternate } = condPath.node;
						// TODO: test ?
						// case: x ? FUNC : x -> x ? window.PACKAGE._.FUNC : x
						if (t.isIdentifier(consequent) && privateFuncs.has(consequent.name)) {
							funcCallCount.set(consequent.name, funcCallCount.get(consequent.name) + 1);
							condPath.get('consequent').replaceWith(createNestedMemberExpression([consequent.name, ...dir]));
						}
						// case: x ? x : FUNC -> x ? x : window.PACKAGE._.FUNC
						if (t.isIdentifier(alternate) && privateFuncs.has(alternate.name)) {
							funcCallCount.set(alternate.name, funcCallCount.get(alternate.name) + 1);
							condPath.get('alternate').replaceWith(createNestedMemberExpression([alternate.name, ...dir]));
						}
					}

					function callExp(callPath) {
						const callee = callPath.node.callee;
						if (t.isIdentifier(callee)) {
							// case: FUNC() -> window.PACKAGE._.FUNC()
							// this case includes `FUNC() in ?: conditional`
							if (privateFuncs.has(callee.name)) {
								funcCallCount.set(callee.name, funcCallCount.get(callee.name) + 1);
								callPath.node.callee = createNestedMemberExpression([callee.name, ...dir]);
							}
							const args = callPath.get('arguments');
							args.forEach(arg => {
								// case: xxx(FUNC) -> xxx(window.PACKAGE._.FUNC)
								if (t.isIdentifier(arg.node) && privateFuncs.has(arg.node.name)) {
									funcCallCount.set(arg.node.name, funcCallCount.get(arg.node.name) + 1);
									arg.replaceWith(createNestedMemberExpression([arg.node.name, ...dir]));
								}
							});
						}
					}

					function objExp(objectPath) {
						objectPath.node.properties.forEach((property) => {
							const {value} = property;
							// TODO: key ?
							// case: x = { x: FUNC } -> x = { x: window.PACKAGE._.FUNC }
							// case: x.x = FUNC -> x.x = window.PACKAGE._.FUNC
							if (t.isIdentifier(value) && privateFuncs.has(value.name)) {
								funcCallCount.set(value.name, funcCallCount.get(value.name) + 1);
								property.value = createNestedMemberExpression([property.value.name, ...dir]);
							}
						});
					}

					function dfs(path) {
						path.traverse({
							AssignmentExpression(assignPath) {assExp(assignPath);},
							MemberExpression(memPath) { memExp(memPath); },
							ConditionalExpression(condPath) { condExp(condPath); },
							CallExpression(callPath) { callExp(callPath); },
							ObjectExpression(objectPath) { objExp(objectPath); }
						});
					}

					// replace private function calls to window.PACKAGE._.FUNC
					dfs(path);


					const callCountArray = Array.from(funcCallCount.entries()).map(([name, count]) =>
						t.objectProperty(t.identifier(name), t.numericLiteral(count))
					), callCountVariable = t.variableDeclaration('var', [
						t.variableDeclarator(
							t.identifier('dlwlrma'),
							t.objectExpression(callCountArray)
						)
					]);
					path.pushContainer('body', callCountVariable);
				}
			}
		}
	};
};

