// @ts-check
const fs = require('fs');
const path = require('path');
const gulp = require('gulp');
const minimist = require('minimist');
/**
 * @typedef {Object} BabelMismatch
 * @property {import('@babel/core').TransformOptions['filename']} patch
 * @property {NonNullable<Parameters<typeof import('gulp-babel')>[0]>['filename']} original
 */
/**
 * gulp-babel seems to be no longer supported, so I patch the type declaration here manually.
 * @type {(options?: import('@babel/core').TransformOptions) => NodeJS.ReadWriteStream}
 *
 * {@link BabelMismatch.patch} is not assignable to {@link BabelMismatch.original}.
 */
// @ts-expect-error: Types of property `filename` are incompatible. See the links above.
const babel = require('gulp-babel');
const rename = require('gulp-rename');
const uglify = require('gulp-uglify');
const browserSync = require('browser-sync').create();
/**
 * @typedef {Object} PrintMismatch
 * @property {NodeJS.ReadWriteStream} patch
 * @property {import('stream').Stream} original
 */
/**
 * gulp-print seems to be no longer supported, so I patch the type declaration here manually.
 * @type {(format?: import('gulp-print').FormatFunction) => NodeJS.ReadWriteStream}
 *
 * {@link PrintMismatch.patch} is not assignable to {@link PrintMismatch.original}.
 */
// @ts-expect-error: Type mismatch due to missing properties. See the links above.
const print = require('gulp-print').default;
/**
 * @typedef {<F extends () => NodeJS.ReadWriteStream, A extends Parameters<F>>(filter: F, args: A) => ReturnType<F>} Through
 */
/**
 * @type {(tapFunction: (file: import('vinyl'), t?: {through?: Through}) => void) => NodeJS.ReadWriteStream}
 */
const tap = require('gulp-tap');
const gulpIgnore = require('gulp-ignore');
const postcss = require('gulp-postcss');
const mergeStream = require('merge-stream');
/**
 * The signature should be `createResolver(config, options).resolve(key, ...)`.
 * @type {(...args: Parameters<typeof import('resolve-options')>) => {resolve: (key: string, ...args: any[]) => number | string | boolean | Date | undefined | null}}
 */
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
	boolean: ['force', 'devMode'],
	default: {
		src: 'zul/src/main/resources/web/js',
		dest: 'zul/build/resources/main/web/js',
		force: false,
		port: 8080,
		devMode: false,
		subprojectPaths: 'zk,zul,../zkcml/zkex,../zkcml/zkmax,../zkcml/client-bind',
	}
};
const options = minimist(process.argv.slice(3), knownOptions);
/**
 * `options.subprojectPaths` is set from "build.gradle" in "zktest" and "zephyr-test".
 * In any case, duplicated paths should be pruned.
 */
options.subprojectPaths = [...new Set(
	/** @type {string} */(options.subprojectPaths).split(',')
)];

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
		const optResolver = createResolver(config, {});
		const cwd = path.resolve(/** @type {string} */(optResolver.resolve('cwd', file)));
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
 * @param {boolean} [force] - Force keep. See {@link ignoreSameFile}
 * @param {number | Date} [since] - Only find files that have been modified since the time specified
 */
function typescript_build(src, dest, force, since) {
	const webpackConfig = require('./webpack.config.js');
	/** @type {import('vinyl-fs').SrcOptions} */
	const defaultSrcOptions = {
		root: src,
		since,
	};
	const tsBundledEntries = gulp.src('/**/index.ts', defaultSrcOptions);

	// Streams are not sequenced. If one uses `ignoreSameFile` on stream 1, but stream 4
	// executes first, then `*.ts` will be excluded from compilation because stream 4 would
	// have already copied the same `*.ts` into `dest`. Thus, I don't rely on ignoreSameFile.
	// Fortunately, stream 1 and stream 2 both produces only `*.js` which stream 4 will not
	// overwrite (stream 4 explicitly ignores `*.js` in `gulp.src()`).
	return mergeStream(
		// Transpile single files with babel which are not siblings of some `index.ts`
		// Only for JS files, because *.ts will be included by `index.ts`
		gulp.src('/**/@(*.js)', { // stream 1
			...defaultSrcOptions,
			ignore: ['/**/*.d.ts'],
		})
			.pipe(gulpIgnore.exclude(
				file => fs.existsSync(path.join(path.dirname(file.path), 'index.ts'))
			))
			.pipe(ignoreSameFile(dest))
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
		// Bundle `index.ts` with webpack as `index.js` with source map
		bundleWithWebpack( // stream 2
			tsBundledEntries, {
				...webpackConfig,
				mode: 'production',
				devtool: 'source-map',
			},
			'js',
		),
		options.devMode ? gulp.src('.') :
		// Bundle `index.ts` with webpack as `index.src.js` without source map
		bundleWithWebpack( // stream 3
			tsBundledEntries, {
				...webpackConfig,
				mode: 'development',
				optimization: {
					minimize: false,
				},
				devtool: false,
			},
			'src.js',
		),
		// fix copy resource in zipjs folder
		gulp.src('/**/!(*.less|*.js|*.d.ts)', { // stream 4
			...defaultSrcOptions,
			nodir: true,
		})
			.pipe(ignoreSameFile(dest))
			.pipe(gulp.dest(dest))
			.pipe(print())
	);

	/**
	 * @param {NodeJS.ReadWriteStream} bundleEntries
	 * @param {webpack.Configuration} webpackConfig
	 * @param {string} fileExtension - `js`, `src.js`, etc.
	 * @returns {NodeJS.ReadWriteStream}
	 */
	function bundleWithWebpack(bundleEntries, webpackConfig, fileExtension) {
		return bundleEntries
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
						},
					},
				};
				webpackConfig.output = {
					filename: `[name].${fileExtension}`,
					path: process.cwd(),
				};
				return webpackStream(webpackConfig, webpack);
			}))
			.pipe(gulp.dest(dest))
			.pipe(print());
	}
}

function browsersync_init(done) {
	browserSync.init({
		proxy: `localhost:${options.port}`,
		startPath: options.startPath,
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
	// WARNING: Don't pass `since`, as the cache liveliness detection is faulty.
	// Only pass `since` if the corretness can be verified in future versions.
	return typescript_build(src, dest)
		.pipe(browserSync.stream());
}

/**
 * @param {string} subprojectPath - E.g., "zul", "../zkcml/client-bind"
 * @param {number | Date} [since] - Only find files that have been modified since the time specified
 */
function typescript_dev_subproject(subprojectPath, since) {
	return typescript_dev(
		path.join(subprojectPath, 'src/main/resources'),
		path.join(subprojectPath, 'build/resources/main'),
		since, // gulp.lastRun(typescript_dev_zk)
		// TODO: `gulp.lastRun` is currently ignored in the downstream due to it being
		// unreliable. Hence, it is safe to ignore it here. See the warning in
		// `typescript_dev`. Read more docs and do more experiments to see how
		// we can make it work.
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
				file.contents = Buffer.from(/** @type {NonNullable<typeof file.contents>} */(file.contents).toString('utf-8')
					.replace(/<%/g, '/*!<%')
					.replace(/\${([^}]*)}/g, (match, g1) => `\\9${g1}\\0`)
					.replace(/<c:/g, '<%c--')
					.replace(/%>/g, '%>*/')
					.replace(/\/>/g, '--c%>'), 'utf-8');
			}
		}))
		.pipe(tap(function (file, t) {
			if (file.path.endsWith('.css.dsp')) {
				return /** @type {Required<NonNullable<typeof t>>} */(t).through(postcss, [[require('cssnano')]]);
			} else {
				console.log('copy...', file.path);
			}
		}))
		.pipe(tap(function (file) {
			if (file.path.endsWith('.css.dsp')) {
				// revert DSP syntax
				file.contents = Buffer.from(/** @type {NonNullable<typeof file.contents>} */(file.contents).toString('utf-8')
					.replace(/\/\*!<%/g, '<%')
					.replace(/\\9([^\\0]*)\\0/g, (match, g1) => `\${${g1}}`)
					.replace(/<%c--/g, '<c:')
					.replace(/--c%>/g, '/>')
					.replace(/%>\*\//g, '%>'), 'utf-8');
			}
		}))
		.pipe(gulp.dest(destDir))
		.pipe(print());
};

const dtsEntry = 'index.d.ts';

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
/**
 * Requires the command line argument `--zk-version <VERSION>`
 */
exports['publish:dts'] = function (done) {
	// Don't name the CLI argument as `version` as, `gulp` will interpret it as
	// `gulp --version` regardless of argument position.
	const { 'zk-version': version } = options;
	if (!version || typeof version !== 'string') {
		console.log('Requires a version string: --zk-version <semVer>');
		console.log('For example: npm run publish:dts -- --zk-version <semVer>');
		return;
	}
	console.log(`version: ${version}`);

	const dtsPackage = {
		name: 'zk-types',
		version,
		license: require('./package.json').license,
		types: dtsEntry,
		dependencies: {
			...require('./package.json').dependencies,
			...require('../zkcml/package.json').dependencies,
		}
	};
	console.log(JSON.stringify(dtsPackage));
	fs.writeFileSync('./build/dts/package.json', JSON.stringify(dtsPackage));
	return done();
};

class Subproject {
	/**
	 * @param {string} subprojectPath - E.g., "zul", "../zkcml/client-bind". With or without
	 * a trailing slash ('/') doesn't matter, as we meticulously use `path.join`.
	 */
	constructor(subprojectPath) {
		this.subprojectPath = subprojectPath;
	}
	get watcher() {
		return () => watch_job(
			path.join(this.subprojectPath, '**/*.ts'),
			this.builder,
		);
	}
	get builder() {
		return () => typescript_dev_subproject(this.subprojectPath);
	}
}

const subprojects = /** @type {string[]} */(options.subprojectPaths)
	.map(subprojectPath => new Subproject(subprojectPath));

exports['build:single'] = typescript_build_single;
exports.watch = gulp.series(
	browsersync_init,
	gulp.parallel(...subprojects.map(subproject => subproject.watcher)),
);
exports.build = gulp.parallel(...subprojects.map(subproject => subproject.builder));
exports.default = exports.build;
