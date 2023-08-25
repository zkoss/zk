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
						exports = {};

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
									exports[declaration.id.name] = declaration.id.name;
								}
							});
						} else if (t.isFunctionDeclaration(node) && t.isIdentifier(node.id)) {
							exports[node.id.name] = node.id.name;
						}
					});

					// add check-exist if statements
					for (let i = dir.length - 2; i > 0; i--) {
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

					// export all global variables and functions
					path.pushContainer('body',
						// window.x.x.x._ = {...}
						t.expressionStatement(
							t.assignmentExpression(
								'=',
								createNestedMemberExpression(dir),
								t.objectExpression(
									Object.entries(exports).map(([k, v]) => {
										return t.objectProperty(t.identifier(k), t.identifier(v));
									})
								)
							)
						)
					);

				}
			}
		}
	};
};