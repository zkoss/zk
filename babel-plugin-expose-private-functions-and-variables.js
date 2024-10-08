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
						privateVars = {},
						privateFuncs = {};

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
						if (t.isVariableDeclaration(node)) {
							node.declarations.forEach((declaration) => {
								if (t.isIdentifier(declaration.id)) {
									privateVars[declaration.id.name] = declaration.id.name;
								}
							});
						} else if (t.isFunctionDeclaration(node) && t.isIdentifier(node.id)) {
							privateFuncs[node.id.name] = node.id.name;
							const functionExpression = t.functionExpression(
								null,
								node.params,
								node.body,
								node.generator || false,
								node.async || false
							);
							const assignment = t.assignmentExpression(
								'=',
								createNestedMemberExpression([node.id.name, ...dir]),
								functionExpression
							);
							path.get('body')[index].replaceWith(t.expressionStatement(assignment));
						}
					});

					// handle assignment to private function properties (e.g., _zk.copy)
					path.traverse({
						AssignmentExpression(assignPath) {
							const left = assignPath.node.left;
							// Check if left-hand side is a member expression like _zk.copy
							if (t.isMemberExpression(left) && t.isIdentifier(left.object) && privateFuncs[left.object.name]) {
								// Replace _zk with window.zk.zk._._zk
								assignPath.node.left.object = createNestedMemberExpression([privateFuncs[left.object.name], ...dir]);
							}
						}
					});

					// add check-exist if statements
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

					// export all global variables
					path.pushContainer('body',
						// window.x.x.x._ = {...}
						t.expressionStatement(
							t.assignmentExpression(
								'=',
								createNestedMemberExpression(dir),
								t.objectExpression(
									Object.entries(privateVars).map(([k, v]) => {
										return t.objectProperty(t.identifier(k), t.identifier(v));
									})
								)
							)
						)
					);

					// replace all private function calls to window.x.x.x._._func()
					path.traverse({
						CallExpression(callPath) {
							const callee = callPath.node.callee;
							if (t.isIdentifier(callee) && privateFuncs[callee.name])
								callPath.node.callee = createNestedMemberExpression([privateFuncs[callee.name], ...dir]);
						}
					});
				}
			}
		}
	};
};