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
						privateFuncs = new Set();

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
							privateFuncs.add(node.id.name);
							path.get('body')[index].replaceWith(
								t.expressionStatement(
									t.assignmentExpression(
										'=',
										createNestedMemberExpression([node.id.name, ...dir]),
										t.functionExpression(
											null,
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

					// replace private function calls to `window.PACKAGE._._func()`
					path.traverse({
						CallExpression(callPath) {
							const callee = callPath.node.callee;
							if (t.isIdentifier(callee) && privateFuncs.has(callee.name))
								callPath.node.callee = createNestedMemberExpression([callee.name, ...dir]);
						}
					});
				}
			}
		}
	};
};
