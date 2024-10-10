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
							if (node.id.name === '_zk') return;
							// if (node.id.name === 'doLog') return;
							privateFuncs.add(node.id.name);
							funcCallCount.set(node.id.name, 0);
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

					// replace private function calls to `window.PACKAGE._._func()`
					path.traverse({
						AssignmentExpression(assignPath) {
							const left = assignPath.node.left,
								right = assignPath.node.right;
							// 如果賦值的右側是一個私有函式名稱，將其替換成 `window.PACKAGE._._func`
							if (t.isIdentifier(right) && privateFuncs.has(right.name)) {
								assignPath.node.right = createNestedMemberExpression([right.name, ...dir]);
								funcCallCount.set(right.name, funcCallCount.get(right.name) + 1);
							}
						},
						CallExpression(callPath) {
							const callee = callPath.node.callee;

							// 替換直接的函式呼叫
							if (t.isIdentifier(callee) && privateFuncs.has(callee.name)) {
								callPath.node.callee = createNestedMemberExpression([callee.name, ...dir]);
								funcCallCount.set(callee.name, funcCallCount.get(callee.name) + 1);
							}

							// 檢查是否是 setTimeout 的函式參數
							if (t.isIdentifier(callee) && callee.name === 'setTimeout') {
								const args = callPath.get('arguments');
								args.forEach(arg => {
									// 檢查是否為 FunctionExpression
									if (t.isFunctionExpression(arg.node)) {
										arg.traverse({
											CallExpression(innerCallPath) {
												const innerCallee = innerCallPath.node.callee;

												// 如果內部的呼叫是私有函式
												if (t.isIdentifier(innerCallee) && privateFuncs.has(innerCallee.name)) {
													innerCallPath.node.callee = createNestedMemberExpression([innerCallee.name, ...dir]);
													funcCallCount.set(innerCallee.name, funcCallCount.get(innerCallee.name) + 1);
												}
											}
										});
									} else if (t.isConditionalExpression(arg.node)) {
										// 處理條件運算子
										const { test, consequent, alternate } = arg.node;

										// 檢查 consequent 和 alternate 是否是 Identifier
										if (t.isIdentifier(consequent) && privateFuncs.has(consequent.name)) {
											arg.get('consequent').replaceWith(createNestedMemberExpression([consequent.name, ...dir]));
											funcCallCount.set(consequent.name, funcCallCount.get(consequent.name) + 1);
										}
										if (t.isIdentifier(alternate) && privateFuncs.has(alternate.name)) {
											arg.get('alternate').replaceWith(createNestedMemberExpression([alternate.name, ...dir]));
											funcCallCount.set(alternate.name, funcCallCount.get(alternate.name) + 1);
										}
									} else if (t.isIdentifier(arg.node) && privateFuncs.has(arg.node.name)) {
										// 替換 setTimeout 中的函式參數
										arg.replaceWith(createNestedMemberExpression([arg.node.name, ...dir]));
										// funcCallCount.set(arg.node.name, funcCallCount.get(arg.node.name) + 1);
									}
								});
							}
						},
						FunctionExpression(funcPath) {
							// 遍歷函式內部的語句，檢查是否使用私有函式
							funcPath.traverse({
								CallExpression(innerCallPath) {
									const callee = innerCallPath.node.callee;

									if (t.isIdentifier(callee) && privateFuncs.has(callee.name)) {
										innerCallPath.node.callee = createNestedMemberExpression([callee.name, ...dir]);
										funcCallCount.set(callee.name, funcCallCount.get(callee.name) + 1);
									}
								}
							});
						},
						ObjectExpression(objectPath) {
							objectPath.node.properties.forEach((property) => {
								// 確保屬性值是 Identifier，且在私有函式中
								if (t.isIdentifier(property.value) && privateFuncs.has(property.value.name)) {
									// 替換屬性值
									property.value = createNestedMemberExpression([property.value.name, ...dir]);
									// funcCallCount.set(property.value.name, funcCallCount.get(property.value.name) + 1);
								}
							});
						}
					});

					// 4. 構建一個變數來輸出呼叫次數結果
					const callCountArray = Array.from(funcCallCount.entries()).map(([name, count]) =>
						t.objectProperty(t.identifier(name), t.numericLiteral(count))
					);
					const callCountVariable = t.variableDeclaration('var', [
						t.variableDeclarator(
							t.identifier('dlwlrmaXXX'),
							t.objectExpression(callCountArray)
						)
					]);
					// 5. 在程式結尾插入計算結果變數
					path.pushContainer('body', callCountVariable);
				}
			}
		}
	};
};

