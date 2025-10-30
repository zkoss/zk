// eslint-disable-next-line no-undef
module.exports = function ({types: t}) {

	/**
	 * @internal
	 * Convert directory string array to MemberExpression.
	 * @param dir - directory string[]
	 * @returns MemberExpression
	 */
	function _createNestedMemberExpression(dir) {
		// example:
		// input -> ['window', 'zk', 'widget_', '_listenFlex']
		// output -> window.zk.widget_._listenFlex
		if (dir.length === 1) {
			return t.identifier(dir[0]);
		} else {
			const tail = dir[dir.length - 1],
				rest = dir.slice(0, -1);
			return t.memberExpression(
				_createNestedMemberExpression(rest),
				t.identifier(tail)
			);
		}
	}

	/**
	 * @internal
	 * Create function AssignmentExpression from a FunctionDeclaration node.
	 * @param dir - directory string[]
	 * @param node - FunctionDeclaration node
	 * @returns ExpressionStatement
	 */
	function _createFunctionAssignmentExpression(dir, node) {
		// example:
		// input -> ['window', 'zk', 'widget_'], node = function _listenFlex(args) {...}
		// output -> window.zk.widget_._listenFlex = function(args) {...}
		return t.expressionStatement(
			t.assignmentExpression(
				'=',
				_createNestedMemberExpression([...dir, node.id.name]),
				t.functionExpression(
					undefined,
					node.params,
					node.body,
					node.generator || false,
					node.async || false
				)
			)
		);
	}

	/**
	 * @internal
	 * Create check-exist if statement for a directory.
	 * @param dir - directory string[]
	 * @returns IfStatement
	 */
	function _createCheckExistIfStatement(dir) {
		// example:
		// input -> ['window', 'zk', 'widget_']
		// output -> if (!window.zk) window.zk = {}; if (!window.zk.widget_) window.zk.widget_ = {};
		const nestedExpression = _createNestedMemberExpression(dir);
		return t.ifStatement(
			t.unaryExpression('!', nestedExpression),
			t.expressionStatement(
				t.assignmentExpression(
					'=',
					nestedExpression,
					t.objectExpression([])
				)
			)
		);
	}

	/**
	 * @internal
	 * Create export private variable statement.
	 * @param dir - directory string[]
	 * @param varName - variable name
	 * @returns ExpressionStatement
	 */
	function _createExportPrivateVariableStatement(dir, varName) {
		// example:
		// input -> ['window', 'zk', 'widget_'], varName = '_listenFlex'
		// output -> window.zk.widget_._listenFlex = _listenFlex
		return t.expressionStatement(
			t.assignmentExpression(
				'=',
				_createNestedMemberExpression([...dir, varName]),
				t.identifier(varName)
			)
		);
	}

	/**
	 * @internal
	 * Check if a node is an exported function.
	 * @param node - node to check
	 * @returns boolean
	 */
	function _isExportedFunction(node) {
		// example:
		// input -> exports.x = x;
		// output -> true
		return t.isExpressionStatement(node) &&
			t.isAssignmentExpression(node.expression) &&
			t.isMemberExpression(node.expression.left) &&
			t.isIdentifier(node.expression.left.object) &&
			node.expression.left.object.name === 'exports' &&
			t.isIdentifier(node.expression.left.property) &&
			t.isIdentifier(node.expression.right);
	}

	/**
	 * @internal
	 * Check if a node is a private function identifier.
	 * @param node - node to check
	 * @param privateFuncsSet - private functions set
	 * @returns boolean
	 */
	function _isPrivateFunctionIdentifier(node, privateFuncsSet) {
		return t.isIdentifier(node) && privateFuncsSet.has(node.name);
	}

	return {
		visitor: {
			Program: {
				exit(rootPath) {
					let _dir = this.file.opts.filename.replace(/-/g, '_').split('/'),
						_jsLoc = _dir.findIndex(x => x === 'js'),
						_file = _dir[_dir.length - 1],
						_privateVars = new Set(),
						_privateFuncs = new Set();

					// pass if [not in js folder] or [not ts file] or [is global.d.ts] or [is index.ts]
					if (_jsLoc === -1 || !_file.endsWith('ts') || _file === 'global.d.ts' || _file === 'index.ts') return;

					// preprocess directory to ['window', '${PACKAGE_PATH}_']
					// e.g. js/zk/widget.ts -> ['window', 'zk', 'widget_']
					_dir = ['window', ..._dir.slice(_jsLoc + 1, -1), _file.replace('.ts', '') + '_'];

					// visit all nodes and collect private variables and functions
					rootPath.node.body.forEach((path) => {
						// collect private variables
						if (t.isVariableDeclaration(path)) {
							path.declarations.forEach((declaration) => {
								if (t.isIdentifier(declaration.id))
									_privateVars.add(declaration.id.name);
							});
						} else if (t.isFunctionDeclaration(path) && t.isIdentifier(path.id)) {
							_privateFuncs.add(path.id.name);
						} else if (_isExportedFunction(path)) {
							// It's guaranteed that the exports statement of the function will ALWAYS come after function declaration
							// e.g.
							// function animating() {...}
							// exports.animating = animating;
							_privateFuncs.delete(path.expression.right.name);
						}
					});

					// replace private function declarations with `window.${PACKAGE_PATH}_._func`
					rootPath.node.body.forEach((path, index) => {
						if (t.isFunctionDeclaration(path)
							&& t.isIdentifier(path.id)
							&& _privateFuncs.has(path.id.name))
							rootPath.get('body')[index].replaceWith(_createFunctionAssignmentExpression(_dir, path));
					});

					// insert check-exist if statements in the start of the file
					for (let i = _dir.length; i >= 2; i--)
						rootPath.unshiftContainer('body', _createCheckExistIfStatement(_dir.slice(0, i)));

					// export private variable to `window.${PACKAGE_PATH}_._var = _var`
					_privateVars.forEach(v => {
						rootPath.pushContainer('body', _createExportPrivateVariableStatement(_dir, v));
					});

					/**
					 * ArrayExpression
					 * properties: elements
					 * example: [x, y, z...]
					 * elements: x, y, z...
					 */
					function arrExp(path) {
						path.get('elements').forEach((element) => {
							dfs(element);

							// case: [FUNC, x, y...] -> [window.${PACKAGE_PATH}_.FUNC, x, y...]
							if (_isPrivateFunctionIdentifier(element.node, _privateFuncs))
								element.replaceWith(_createNestedMemberExpression([..._dir, element.node.name]));
						});
					}

					/**
					 * AssignmentExpression
					 * properties: left, right
					 * example: x = y
					 * left: x, right: y
					 */
					function assExp(path) {
						dfs(path.get('left'));
						dfs(path.get('right'));

						const { left, right } = path.node;
						// case: FUNC = x -> window.${PACKAGE_PATH}_.FUNC = x
						if (_isPrivateFunctionIdentifier(left, _privateFuncs))
							path.node.left = _createNestedMemberExpression([..._dir, left.name]);
						// case: x = FUNC -> x = window.${PACKAGE_PATH}_.FUNC
						if (_isPrivateFunctionIdentifier(right, _privateFuncs))
							path.node.right = _createNestedMemberExpression([..._dir, right.name]);
					}

					/**
					 * CallExpression
					 * properties: callee, arguments
					 * example: x(y...)
					 * callee: x, arguments: y...
					 */
					function callExp(path) {
						dfs(path.get('callee'));

						const { callee } = path.node;
						// case: FUNC() -> window.${PACKAGE_PATH}_.FUNC()
						if (_isPrivateFunctionIdentifier(callee, _privateFuncs))
							path.node.callee = _createNestedMemberExpression([..._dir, callee.name]);

						const args = path.get('arguments');
						args.forEach(arg => {
							dfs(arg);

							// case: xxx(FUNC...) -> xxx(window.${PACKAGE_PATH}_.FUNC...)
							if (_isPrivateFunctionIdentifier(arg.node, _privateFuncs))
								arg.replaceWith(_createNestedMemberExpression([..._dir, arg.node.name]));
						});
					}

					/**
					 * ConditionalExpression
					 * properties: test, consequent, alternate
					 * example: x ? y : z
					 * test: x, consequent: y, alternate: z
					 */
					function condExp(path) {
						dfs(path.get('test'));
						dfs(path.get('consequent'));
						dfs(path.get('alternate'));

						const { test, consequent, alternate } = path.node;
						// case: FUNC ? x : y -> window.${PACKAGE_PATH}_.FUNC ? x : y
						if (_isPrivateFunctionIdentifier(test, _privateFuncs))
							path.node.test = _createNestedMemberExpression([..._dir, test.name]);
						// case: x ? FUNC : x -> x ? window.${PACKAGE_PATH}_.FUNC : x
						if (_isPrivateFunctionIdentifier(consequent, _privateFuncs))
							path.node.consequent = _createNestedMemberExpression([..._dir, consequent.name]);
						// case: x ? x : FUNC -> x ? x : window.${PACKAGE_PATH}_.FUNC
						if (_isPrivateFunctionIdentifier(alternate, _privateFuncs))
							path.node.alternate = _createNestedMemberExpression([..._dir, alternate.name]);
					}

					/**
					 * LogicalExpression
					 * properties: left, right
					 * example: x && y, x || y, x ?? y
					 * left: x, right: y
					 */
					function logicExp(path) {
						dfs(path.get('left'));
						dfs(path.get('right'));

						const { left, right } = path.node;
						// case: FUNC && x -> window.${PACKAGE_PATH}_.FUNC && x
						if (_isPrivateFunctionIdentifier(left, _privateFuncs))
							path.node.left = _createNestedMemberExpression([..._dir, left.name]);
						// case: x && FUNC -> x && window.${PACKAGE_PATH}_.FUNC
						if (_isPrivateFunctionIdentifier(right, _privateFuncs))
							path.node.right = _createNestedMemberExpression([..._dir, right.name]);
					}

					/**
					 * MemberExpression
					 * properties: object, property
					 * example: x.y.z
					 * object: x.y, property: z
					 */
					function memExp(path) {
						dfs(path.get('object'));
						// [NOTE] property cannot replace with window.${PACKAGE_PATH}_.FUNC, so ignore

						const object = path.node.object;
						// case: FUNC.x -> window.${PACKAGE_PATH}_.FUNC.x
						if (_isPrivateFunctionIdentifier(object, _privateFuncs))
							path.node.object = _createNestedMemberExpression([..._dir, object.name]);
					}

					/**
					 * ObjectExpression
					 * properties: properties
					 */
					function objExp(path) {
						path.node.properties.forEach((property) => {
							const {value} = property;
							// case: x = { x: FUNC } -> x = { x: window.${PACKAGE_PATH}_.FUNC }
							// case: x.x = FUNC -> x.x = window.${PACKAGE_PATH}_.FUNC
							if (_isPrivateFunctionIdentifier(value, _privateFuncs))
								property.value = _createNestedMemberExpression([..._dir, value.name]);
						});
					}

					/**
					 * VariableDeclaration
					 * properties: declarations
					 */
					function varDecl(path) {
						// WARNING: cannot use `path.get('declarations')` to iterate here, will get wrong result
						path.node.declarations.forEach((declaration) => {
							// [NOTE] declaration.get('init') and declaration.init in dfs are dead

							const { init } = declaration;
							// case: x = FUNC -> x = window.${PACKAGE_PATH}_.FUNC
							if (_isPrivateFunctionIdentifier(init, _privateFuncs))
								declaration.init = _createNestedMemberExpression([..._dir, init.name]);
						});
					}

					/**
					 * Traverse all nodes in current file
					 */
					function dfs(path) {
						path.traverse({
							ArrayExpression(p) { arrExp(p); },
							AssignmentExpression(p) {assExp(p); },
							CallExpression(p) { callExp(p); },
							ConditionalExpression(p) { condExp(p); },
							LogicalExpression(p) { logicExp(p); },
							MemberExpression(p) { memExp(p); },
							ObjectExpression(p) { objExp(p); },
							VariableDeclaration(p) { varDecl(p); }
						});
					}

					// replace private function calls to window.${PACKAGE_PATH}_.FUNC
					dfs(rootPath);
				}
			}
		}
	};
};

