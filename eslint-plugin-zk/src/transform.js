// jscodeshift can take a parser, like "babel", "babylon", "flow", "ts", or "tsx"
// Read more: https://github.com/facebook/jscodeshift#parser
export const parser = 'tsx';

// styles:
// `this.proxy(this._onRespReady)` => `this._onRespReady.bind(this)`
// DONE: `!a.$instanceof(A, B)` => `!(a instanceof A || a instanceof B)`
// DONE: `!A.isInstance(a)` => `!(a instanceof A)`
// `$supers` => `super`
// setter must return this
// apply rule

function findParent(path, type) {
	let curPath = path;
	do {
		if (curPath.get('type').value === type) {
			return curPath;
		}
		curPath = curPath.parentPath;
	} while (curPath);
}

function expandMemberExpression(node) {
	return node.object ? `${expandMemberExpression(node.object)}.${node.property.name}` : node.name;
}


// Press ctrl+space for code completion
export default function transformer(file, api) {
	const j = api.jscodeshift;
	const root = j(file.source);
	const aliases = new Map();

	const zkClassAssignments = root
		.find(j.AssignmentExpression, {
			right: {
				type: 'CallExpression',
				callee: {
					object: { name: 'zk' },
					property: { name: '$extends' },
				}
			}
		})
		.forEach(path => {
			const left = path.node.left;
			const classToExport = zkClassDefinition(left.property.name, path.node.right);
			const decorators = classToExport.decorators = classToExport.decorators || [];
			decorators.push(j.decorator(j.identifier(`zk.WrapClass('${expandMemberExpression(left)}')`)));
			j(path).replaceWith(j.exportDeclaration(false, classToExport));
			const expression = findParent(path, 'ExpressionStatement');
			console.log(expression);
		});
	return root.toSource();

	function zkClassDefinition(name, extendsCallNode) {
		const [baseClass, nonstaticMembers, staticMembers] = extendsCallNode.arguments;

		const methods = zkMemberMethods(nonstaticMembers.properties, false);
		if (staticMembers) {
			methods.push(...zkMemberMethods(staticMembers.properties, true));
		}
		return j.classDeclaration(j.identifier(name), j.classBody(methods), baseClass);
	}
	/*  
  .find(j.Identifier)
	.forEach(path => {
	  j(path).replaceWith(
		j.identifier(path.node.name.split('').reverse().join(''))
	  );
	});
	*/
	function zkMemberMethods(members, isStatic) {
		if (!members) {
			return [];
		}
		const methods = [];
		for (const member of members) {
			let name = member.key.name;
			if (!member.value) {
				console.log(member);
			}
			if (member.type === 'ObjectMethod') {
				member.value = j.functionExpression.from({
					body: member.body,
					params: member.params,
					returnType: member.returnType
				});
			}
			if (member.value.type === 'FunctionExpression') {
				let kind = 'method';
				if (name === '$init') {
					name = kind = 'constructor';
				}

				name = getFullName(name, isStatic);
				const method = j.methodDefinition(kind, j.identifier(name), member.value, false);
				method.comments = member.comments;
				if (kind === 'constructor') {
					method.value.body.body.unshift(
						j.expressionStatement(j.identifier('super()'))
					);
				} else {
					let comment = getFirstOfArray(method.comments);
					if (comment) {
						comment = comment.value;
					}
					method.value.returnType = method.value.returnType || `: ${parseReturnType(comment) || 'void'}`;
					const paramTypes = parseParameterType(comment).values();
					for (const param of method.value.params) {
						const { done, value } = paramTypes.next();
						if (param.typeAnnotation) {
							continue;
						}
						const paramType = done || !value ? 'void' : value;
						param.name = `${param.name}: ${paramType}`;
					}
				}
				methods.push(method);
			} else {
				if (name === '$define') {
					methods.push(...member.value.properties.map(zkDefine).flat());
					continue;
				}
				name = getFullName(name, isStatic);
				const property = j.classProperty(j.identifier(name), member.value);
				property.comments = member.leadingComments;
				methods.push(property);
			}
		}
		return methods;
	}

	function getFullName(name, isStatic = false) {
		return `${isStatic ? 'static ' : ''}${name}`;
	}

	function zkDefine(item) {
		if (item.type === 'ObjectMethod') {
			item.value = j.functionExpression.from({
				body: item.body,
				params: item.params,
				returnType: item.returnType
			});
		}
		const {
			key: { name },
			value: { type }
		} = item;
		const _name = `_${name}`;
		const Name = name.charAt(0).toUpperCase() + name.slice(1);
		// zk.ts 994
		const {
			getterFirst,
			getterComment,
			setterComment
		} = sortDefineComments(item.comments);

		const defaultValue = parseDefaultValue(getterComment);
		const paramType = parseParameterType(setterComment);
		const fieldType = parseReturnType(getterComment) ||
			paramType[0] || 'void';
		const augmentedFieldType =
			(defaultValue === 'undefined') ?
				`${fieldType} | ${defaultValue}` : fieldType;
		const getterPrefix = fieldType === 'boolean' ? 'is' : 'get';
		const field = j.classProperty(
			j.identifier(`${_name}: ${augmentedFieldType}`),
			defaultValue !== 'undefined' ? j.identifier(defaultValue) : null
		);

		const getter = j.methodDefinition(
			'method',
			j.identifier(`${getterPrefix}${Name}`),
			j.functionExpression(
				null,
				[],
				j.blockStatement([
					j.returnStatement(j.identifier(`this.${_name}`)),
				])
			),
			false
		);
		if (getterComment) {
			getter.comments = [j.commentBlock(getterComment)];
		}
		getter.value.returnType = `: ${augmentedFieldType}`;

		let paramName = parseParameterName(setterComment, name);
		const statements = [];
		const defSet00 = () => {
			statements.push(
				j.expressionStatement(j.identifier(`this.${_name} = ${paramName}`)),
				j.returnStatement(j.thisExpression())
			);
		};
		const defSet01 = (after) => {
			if (after.params.length && after.params[0].name) {
				paramName = after.params[0].name;
			}
			statements.push(
				j.variableDeclaration('const', [
					j.variableDeclarator(j.identifier(`o = this.${_name}`), null)
				])
			);
			defSet00();
			statements.splice(2, 0, j.ifStatement(
				j.identifier(`o !== ${paramName} || opts?.force`),
				after.body
			));
		};
		const defSet11 = (before, after) => {
			if (after) {
				defSet01(after);
			}
			statements.splice(
				1,
				0,
				j.expressionStatement(
					j.assignmentExpression(
						'=',
						j.identifier(paramName),
						j.callExpression(
							before,
							[
								j.identifier(paramName),
								j.identifier('opts')
							]
						)
					)
				)
			);
		};

		(function defSet(expression) {
			const type = expression.type;
			if (type === 'NullLiteral') {
				defSet00();
			} else if (type === 'FunctionExpression') {
				defSet01(expression);
			} else if (type === 'ArrayExpression') {
				const [before, after] = expression.elements;
				defSet11(before, after);
			} else if (type === 'AssignmentExpression') {
				const { left, right } = expression;
				aliases.set(left.name, right);
				defSet(right);
			} else {
				defSet(aliases.get(expression.name));
			}
		})(item.value);

		const setterParams = type === 'NullLiteral' ?
			[j.identifier(`${paramName}: ${fieldType}`)] :
			[j.identifier(`${paramName}: ${fieldType}`), j.identifier('opts?: Record<string, boolean>')];
		const setter = j.methodDefinition(
			'method',
			j.identifier(`set${Name}`),
			j.functionExpression(null, setterParams, j.blockStatement(statements)),
			false
		);
		if (setterComment) {
			setter.comments = [j.commentBlock(setterComment)];
		}
		setter.value.returnType = ': this';

		return getterFirst ? [field, getter, setter] : [field, setter, getter];
	}
}

function getFirstOfArray(array) {
	return (!Array.isArray(array) || array.length === 0) ? undefined : array[0];
}

function sortDefineComments(comments) {
	if (!comments || comments.length === 0) {
		return {
			getterFirst: true,
			getterComment: '',
			setterComment: '',
		};
	}
	let getterComment = comments[0].value.replaceAll(/\n\s+/g, '\n ');
	let setterComment = comments[1] && comments[1].value.replaceAll(/\n\s+/g, '\n ');
	const getterFirst = getterComment.match(/\*\s+@return/) !== null;
	if (!getterFirst) {
		[getterComment, setterComment] = [setterComment, getterComment];
	}
	getterComment = getterComment || '';
	setterComment = setterComment || '';
	return { getterFirst, getterComment, setterComment };
}

function parseType(type) {
	switch (type) {
		case 'String':
			return 'string';
		case 'int':
		case 'float':
		case 'double':
			return 'number';
	}
	return type;
}

function parseReturnType(comment) {
	if (!comment) {
		return null;
	}
	const match = comment.match(/\*\s+@return\s+(\w+)/);
	return match ? parseType(match[1]) : null;
}

function parseDefaultValue(comment) {
	if (!comment) {
		return 'undefined';
	}
	const match = comment.match(/Default:\s+([^.\s]+)/);
	return match ? match[1] : 'undefined';
}

function parseParameterName(comment, name) {
	name = name || 'v';
	if (!comment) {
		return name;
	}
	const match = comment.match(/\*\s+@param\s+\w+\s+(\w+)/);
	return match ? match[1] : name;
}

function parseParameterType(comment) {
	if (!comment) {
		return [];
	}
	const matches = comment.matchAll(/\*\s+@param\s+(\w+)/g);
	if (!matches) {
		return [];
	}
	const types = [];
	for (const match of matches) {
		types.push(parseType(match[1]));
	}
	return types;
}

// https://astexplorer.net/#/gist/40a5d9c9adc459eaa79f4ef9c5c3171c/latest