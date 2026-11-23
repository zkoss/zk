var fs = require('fs');
var path = require('path');
var gulp = require('gulp');
var minimist = require('minimist');
var babel = require('gulp-babel');
var rename = require('gulp-rename');
var uglify = require('gulp-uglify');
var browserSync = require('browser-sync').create();
var print = require('gulp-print').default;
var tap = require('gulp-tap');
var gulpIgnore = require('gulp-ignore');
var postcss = require('gulp-postcss');
var mergeStream = require('merge-stream');
var createResolver = require('resolve-options');

var knownOptions = {
    string: ['src', 'dest'],
    number: ['port'],
	boolean: ['force'],
    default: {
        src: 'zul/src/archive/web/js',
        dest: 'zul/codegen/archive/web/js',
	    force: false,
        port: 8080
    }
};
var options = minimist(process.argv.slice(3), knownOptions);

// Workaround for maven frontend-maven-plugin passing quoted strings
function stripQuotes(txt) {
    if (txt.charAt(0) === '"' && txt.charAt(txt.length - 1) === '"') {
        return txt.substring(1, txt.length - 1);
    }
    return txt;
}

function watch_job(glob, job) {
    var watcher = gulp.watch(glob, {ignoreInitial: false}, job);
    watcher.on('change', function (path) {
        // eslint-disable-next-line no-console
        console.log('Detect file change: ' + path + '...');
    });
    return watcher;
}

var config = {
	cwd: {
		type: 'string',
		default: process.cwd,
	}
};

function ignoreSameFile(destDir, force) {
	return gulpIgnore.exclude(function (file) {
		if (force) return false;
		// simulate gulp.dest() to find a output path
		var optResolver = createResolver(config);
		var cwd = path.resolve(optResolver.resolve('cwd', file));
		var basePath = path.resolve(cwd, destDir);
		var writePath = path.resolve(basePath, file.relative);
		if (fs.existsSync(writePath)) {
			var srcStat = fs.statSync(file.path);
			var outStat = fs.statSync(writePath);
			if (srcStat.mtime <= outStat.mtime) {
				return true;
			}
		}
		return false;
	});
}

function typescript_build_single() {
    var sources = stripQuotes(options.src),
	    destDir = stripQuotes(options.dest),
	    force = options.force;
    return typescript_build(sources, destDir, force);
}

function typescript_build(src, dest, force) {
	return mergeStream(gulp.src([src + '/**/*.ts', src + '/**/*.js'])
	    .pipe(ignoreSameFile(dest, force))
	    .pipe(tap(function(file) {
			if (file.path.endsWith('.js') && file.path.includes('/mold/')) {
				var name = file.basename;
				name = name.substring(0, name.length - 3);
				file.contents = Buffer.concat([
					Buffer.from(name.replace(/-/g, '') + '$mold$ = \n'),
					file.contents
				]);
			}
	    }))
	    .pipe(babel({
		    root: __dirname
	    }))
        .pipe(rename({suffix: '.src'}))
        .pipe(gulp.dest(dest))
        .pipe(uglify())
        .pipe(rename(function (path) {
            path.basename = path.basename.replace(/\.src/, '');
        }))
        .pipe(gulp.dest(dest))
        .pipe(print()),
		// fix copy resource in zipjs folder
		gulp.src(src + '/**/!(*.less|*.js)')
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

function typescript_dev(src, dest, since) {
    return gulp.src(src + '/**/*.ts', {since: since})
        .pipe(print())
        .pipe(babel({
            root: __dirname
        }))
        .pipe(gulp.dest(dest))
        .pipe(rename({suffix: '.src'}))
        .pipe(gulp.dest(dest))
        .pipe(browserSync.stream());
}

function typescript_dev_zk() {
    return typescript_dev(
        'zk/src/archive',
        'zk/debug/classes',
        gulp.lastRun(typescript_dev_zk)
    );
}

function typescript_dev_zul() {
    return typescript_dev(
        'zul/src/archive',
        'zul/debug/classes',
        gulp.lastRun(typescript_dev_zul)
    );
}

function typescript_dev_zkex() {
    return typescript_dev(
        '../zkcml/zkex/src/archive',
        '../zkcml/zkex/debug/classes',
        gulp.lastRun(typescript_dev_zkex)
    );
}

function typescript_dev_zkmax() {
    return typescript_dev(
        '../zkcml/zkmax/src/archive',
        '../zkcml/zkmax/debug/classes',
        gulp.lastRun(typescript_dev_zkmax)
    );
}
exports['build:minify-css'] = function () {
	var sources = stripQuotes(options.src),
		destDir = stripQuotes(options.dest),
		force = options.force;
	if (!fs.existsSync(sources)) {
		return gulp.src('.');// ignore
	}
	return gulp.src(sources + '/**/**')
		.pipe(ignoreSameFile(destDir, force))
		.pipe(tap(function(file) {
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
		.pipe(tap(function(file, t) {
			if (file.path.endsWith('.css.dsp')) {
				return t.through(postcss, [[ require('cssnano') ]]);
			} else {
				console.log('copy...', file.path);
			}
		}))
		.pipe(tap(function(file) {
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
            'zk/src/archive/web/js',
            'zk/codegen/archive/web/js');
    },
    function build_zul() {
        return typescript_dev(
            'zul/src/archive/web/js',
            'zul/codegen/archive/web/js');
    },
    function build_zkex() {
        return typescript_dev(
            '../zkcml/zkex/src/archive/web/js',
            '../zkcml/zkex/codegen/archive/web/js');
    },
    function build_zkmax() {
        return typescript_dev(
            '../zkcml/zkmax/src/archive/web/js',
            '../zkcml/zkmax/codegen/archive/web/js');
    }
);
exports.default = exports.build;
