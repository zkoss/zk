/* eslint-disable one-var */
//@ts-check
const fs = require('fs');
const path = require('path');
const gulp = require('gulp');
const minimist = require('minimist');
const babel = require('gulp-babel');
const rename = require('gulp-rename');
const uglify = require('gulp-uglify');
const browserSync = require('browser-sync').create();
const print = require('gulp-print').default;
const tap = require('gulp-tap');
const gulpIgnore = require('gulp-ignore');
const postcss = require('gulp-postcss');
const mergeStream = require('merge-stream');
const createResolver = require('resolve-options');
const webpackStream = require('webpack-stream');
const webpack = require('webpack');
const flatmap = require('gulp-flatmap');
const ts = require('gulp-typescript');
const concat = require('gulp-concat');
const vinylMap = require('vinyl-map');

const knownOptions = {
	string: ['src', 'dest'],
	number: ['port'],
	boolean: ['force'],
	default: {
		src: 'zul/src/main/resources/web/js',
		dest: 'zul/codegen/resources/web/js',
		force: false,
		port: 8080
	}
};
const options = minimist(process.argv.slice(3), knownOptions);

/**
 * Workaround for maven frontend-maven-plugin passing quoted strings
 * @param {string} txt
 */
function stripQuotes(txt) {
	if (txt.startsWith('"') && txt.endsWith('"')) {
		return txt.substring(1, txt.length - 1);
	}
	return txt;
}

function watch_job(glob, job) {
	const watcher = gulp.watch(glob, { ignoreInitial: false }, job);
	watcher.on('change', function (path) {
		console.log('Detect file change: ' + path + '...');
	});
	return watcher;
}

const config = {
	cwd: {
		type: 'string',
		default: process.cwd,
	}
};

/**
 * @param {string} destDir - Output directory
 * @param {boolean} [force] - Force keep
 */
function ignoreSameFile(destDir, force) {
	return gulpIgnore.exclude(function (file) {
		if (force) return false;
		// simulate gulp.dest() to find a output path
		const optResolver = createResolver(config);
		const cwd = path.resolve(optResolver.resolve('cwd', file));
		const basePath = path.resolve(cwd, destDir);
		const writePath = path.resolve(basePath, file.relative);
		if (fs.existsSync(writePath)) {
			const srcStat = fs.statSync(file.path);
			const outStat = fs.statSync(writePath);
			if (srcStat.mtime <= outStat.mtime) {
				return true;
			}
		}
		return false;
	});
}

/**
 * Used by gradle task `compileTypeScript`
 */
function typescript_build_single() {
	const sources = stripQuotes(options.src);
	const destDir = stripQuotes(options.dest);
	const force = options.force;
	return typescript_build(sources, destDir, force);
}

/**
 * See {@link typescript_build_single}
 * @param {string} src - Directory containing JS/TS sources
 * @param {string} dest - Output directory
 * @param {boolean} [force] - Force keep. See {@link ignoreSameFile}.
 */
function typescript_build(src, dest, force) {
	const webpackConfig = require('./webpack.config.js');
	webpackConfig.mode = 'production';
	// Streams are not sequenced. If one uses `ignoreSameFile` on stream 1, but stream 3
	// executes first, then `*.ts` will be excluded from compilation because stream 3 would
	// have already copied the same `*.ts` into `dest`. Thus, I don't rely on ignoreSameFile.
	// Fortunately, stream 1 and stream 2 both produces only `*.js` which stream 3 will not
	// overwrite (stream 3 explicitly ignores `*.js` in `gulp.src()`).
	return mergeStream(
		// Transpile single files with babel which are not siblings of some `index.ts`
		gulp.src('/**/@(*.ts|*.js)', { // stream 1
			root: src,
			ignore: ['/**/*.d.ts'],
		})
			.pipe(gulpIgnore.exclude(
				file => fs.existsSync(path.join(path.dirname(file.path), 'index.ts'))
			))
			.pipe(babel({
				root: __dirname
			}))
			.pipe(rename({ suffix: '.src' }))
			.pipe(gulp.dest(dest))
			.pipe(uglify())
			.pipe(rename(function (path) {
				path.basename = path.basename.replace(/\.src/, '');
			}))
			.pipe(gulp.dest(dest))
			.pipe(print()),
		// Bundle `index.ts` with webpack
		gulp.src('/**/index.ts', { // stream 2
			root: src,
		})
			// There is no official way to specify the "library" property in a
			// webpack "entry" from webpack-stream, so we manipulate the stream
			// manually; note that specifying the "library" property in "output"
			// doesn't work. However, webpack-stream doesn't implement _readableState
			// which gulp-tap reqires. Thus, we use gulp-flatmap.
			.pipe(flatmap(function (stream, file) {
				const entryName = path.join(
					path.dirname(file.relative),
					path.basename(file.path, path.extname(file.path))
				);
				webpackConfig.entry = {
					[entryName]: {
						import: file.path,
						library: {
							type: 'window',
							export: 'default',
						}
					}
				};
				webpackConfig.output = {
					filename: '[name].js',
					path: process.cwd(),
				};
				return webpackStream(webpackConfig, webpack);
			}))
			.pipe(gulp.dest(dest))
			.pipe(print()),
		// fix copy resource in zipjs folder
		gulp.src('/**/!(*.less|*.js|*.d.ts)', { // stream 3
			root: src,
			nodir: true,
		})
			.pipe(ignoreSameFile(dest))
			.pipe(gulp.dest(dest))
			.pipe(print())
	);
}

function browsersync_init(done) {
	browserSync.init({
		proxy: `localhost:${options.port}`
	});
	done();
}

/**
 * @param {string} src - Directory containing JS/TS sources
 * @param {string} dest - Output directory
 * @param {number | Date} [since] - Only find files that have been modified since the time specified
 * @returns {NodeJS.WritableStream}
 */
function typescript_dev(src, dest, since) {
	return gulp.src(src + '/**/*.ts', { since: since })
		.pipe(print())
		.pipe(babel({
			root: __dirname
		}))
		.pipe(gulp.dest(dest))
		.pipe(rename({ suffix: '.src' }))
		.pipe(gulp.dest(dest))
		.pipe(browserSync.stream());
}

function typescript_dev_zk() {
	return typescript_dev(
		'zk/src/main/resources',
		'zk/codegen/resources',
		gulp.lastRun(typescript_dev_zk)
	);
}

function typescript_dev_zul() {
	return typescript_dev(
		'zul/src/main/resources',
		'zul/codegen/resources',
		gulp.lastRun(typescript_dev_zul)
	);
}

function typescript_dev_zkex() {
	return typescript_dev(
		'../zkcml/zkex/src/main/resources',
		'../zkcml/zkex/codegen/resources',
		gulp.lastRun(typescript_dev_zkex)
	);
}

function typescript_dev_zkmax() {
	return typescript_dev(
		'../zkcml/zkmax/src/main/resources',
		'../zkcml/zkmax/codegen/resources',
		gulp.lastRun(typescript_dev_zkmax)
	);
}
exports['build:minify-css'] = function () {
	const sources = stripQuotes(options.src);
	const destDir = stripQuotes(options.dest);
	const force = options.force;
	if (!fs.existsSync(sources)) {
		return gulp.src('.');// ignore
	}
	return gulp.src(sources + '/**/**')
		.pipe(ignoreSameFile(destDir, force))
		.pipe(tap(function (file) {
			if (file.path.endsWith('.css.dsp')) {
				// ignore DSP syntax
				file.contents = Buffer.from(file.contents.toString('utf-8')
					.replace(/<%/g, '/*!<%')
					.replace(/\${([^}]*)}/g, function (match, g1) {
						return '\\9' + g1 + '\\0';
					})
					.replace(/<c:/g, '<%c--')
					.replace(/%>/g, '%>*/')
					.replace(/\/>/g, '--c%>'), 'utf-8');
			}
		}))
		.pipe(tap(function (file, t) {
			if (file.path.endsWith('.css.dsp')) {
				return t.through(postcss, [[require('cssnano')]]);
			} else {
				console.log('copy...', file.path);
			}
		}))
		.pipe(tap(function (file) {
			if (file.path.endsWith('.css.dsp')) {
				// revert DSP syntax
				file.contents = Buffer.from(file.contents.toString('utf-8')
					.replace(/\/\*!<%/g, '<%')
					.replace(/\\9([^\\0]*)\\0/g, function (match, g1) {
						return '${' + g1 + '}';
					})
					.replace(/<%c--/g, '<c:')
					.replace(/--c%>/g, '/>')
					.replace(/%>\*\//g, '%>'), 'utf-8');
			}
		}))
		.pipe(gulp.dest(destDir))
		.pipe(print());
};

const dtsEntry = 'index.d.ts';
/**
 * Requires the command line argument `--version=<VERSION>`
 */
exports['build:dts'] = function () {
	const { declarationDir } = require('./tsconfig.dts.json').compilerOptions;

	const zkDTS = ts.createProject('./tsconfig.dts.json');
	const zkcmlDTS = ts.createProject('../zkcml/tsconfig.dts.json');
	const zkProjectDirs = require('./tsconfig.json').include;
	const zkcmlProjectDirs = require('../zkcml/tsconfig.json').include.map(dir => path.join('../zkcml', dir));

	return mergeStream(
		zkDTS.src()
			.pipe(zkDTS())
			.dts
			.pipe(gulp.dest(declarationDir)),
		zkcmlDTS.src()
			.pipe(zkcmlDTS())
			.dts
			.pipe(gulp.dest(declarationDir)),
		gulp.src(zkProjectDirs.map(dir => dir + '/**/*.d.ts'), { base: '.' })
			.pipe(gulp.dest(declarationDir))
			.pipe(print()),
		gulp.src(zkcmlProjectDirs.map(dir => dir + '/**/*.d.ts'), { base: '../zkcml' })
			.pipe(gulp.dest(declarationDir))
			.pipe(print()),
		gulp.src(['./*/package.json', '../zkcml/*/package.json'])
			.pipe(print())
			.pipe(vinylMap((contents, filename) => {
				const { types } = JSON.parse(contents.toString());
				if (!types) {
					return '';
				}
				const dir = path.basename(path.dirname(filename));
				return `/// <reference path="./${path.join(dir, types)}" />`;
			}))
			.pipe(concat(dtsEntry))
			.pipe(gulp.dest(declarationDir))
			.pipe(print()),
	);
};
exports['publish:dts'] = function (done) {
	const { version } = options;
	if (!version || typeof version !== 'string') {
		console.log('Requires a version string!');
		return;
	}
	console.log(`version: ${version}`);

	const dtsPackage = {
		name: 'zk-types',
		version,
		types: dtsEntry,
		dependencies: {
			...require('./package.json').dependencies,
			...require('../zkcml/package.json').dependencies,
		}
	};
	fs.writeFileSync('./build/dts/package.json', JSON.stringify(dtsPackage));
	return done();
};

exports['build:single'] = typescript_build_single;
exports.watch = gulp.series(
	browsersync_init,
	gulp.parallel(
		() => watch_job('zk/src/**/*.ts', typescript_dev_zk),
		() => watch_job('zul/src/**/*.ts', typescript_dev_zul),
		() => watch_job('../zkcml/zkex/src/**/*.ts', typescript_dev_zkex),
		() => watch_job('../zkcml/zkmax/src/**/*.ts', typescript_dev_zkmax),
	)
);
exports.build = gulp.parallel(
	function build_zk() {
		return typescript_dev(
			'zk/src/main/resources/web/js',
			'zk/codegen/resources/web/js');
	},
	function build_zul() {
		return typescript_dev(
			'zul/src/main/resources/web/js',
			'zul/codegen/resources/web/js');
	},
	function build_zkex() {
		return typescript_dev(
			'../zkcml/zkex/src/main/resources/web/js',
			'../zkcml/zkex/codegen/resources/web/js');
	},
	function build_zkmax() {
		return typescript_dev(
			'../zkcml/zkmax/src/main/resources/web/js',
			'../zkcml/zkmax/codegen/resources/web/js');
	}
);
exports.default = exports.build;
