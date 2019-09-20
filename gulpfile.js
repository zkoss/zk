var gulp = require('gulp');
var minimist = require('minimist');
var babel = require('gulp-babel');
var sourcemaps = require('gulp-sourcemaps');
var rename = require('gulp-rename');
var uglify = require('gulp-uglify');
var browserSync = require('browser-sync').create();
var print = require('gulp-print').default;

var knownOptions = {
    string: ['src', 'dest'],
    number: ['port'],
    default: {
        src: 'zul/src/archive/web/js',
        dest: 'zul/codegen/archive/web/js',
        port: 8080
    }
};
var options = minimist(process.argv.slice(2), knownOptions);

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

function typescript_build_single() {
    var sources = stripQuotes(options.src),
        destDir = stripQuotes(options.dest);
    return typescript_build(sources, destDir);
}

function typescript_build(src, dest) {
    return gulp.src(src + '/**/*.ts')
        .pipe(sourcemaps.init())
        .pipe(babel({
            root: __dirname
        }))
        .pipe(rename({suffix: '.src'}))
        .pipe(gulp.dest(dest))
        .pipe(uglify())
        .pipe(rename(function (path) {
            path.basename = path.basename.replace(/\.src/, '');
        }))
        .pipe(sourcemaps.write('.', {addComment: false, includeContent: false}))
        .pipe(gulp.dest(dest))
        .pipe(print());
}

function typescript_build_zul() {
    return typescript_build(
        'zul/src/archive/web/js',
        'zul/codegen/archive/web/js'
    );
}

function typescript_build_zkex() {
    return typescript_build(
        '../zkcml/zkex/src/archive/web/js',
        '../zkcml/zkex/codegen/archive/web/js'
    );
}

function typescript_build_zkmax() {
    return typescript_build(
        '../zkcml/zkmax/src/archive/web/js',
        '../zkcml/zkmax/codegen/archive/web/js'
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
        .pipe(babel({
            root: __dirname
        }))
        .pipe(gulp.dest(dest))
        .pipe(rename({suffix: '.src'}))
        .pipe(gulp.dest(dest))
        .pipe(print())
        .pipe(browserSync.stream());
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

function watch_typescript_zul() {
    return watch_job('zul/src/**/*.ts', typescript_dev_zul);
}

function watch_typescript_zkex() {
    return watch_job('../zkcml/zkex/src/**/*.ts', typescript_dev_zkex);
}

function watch_typescript_zkmax() {
    return watch_job('../zkcml/zkmax/src/**/*.ts', typescript_dev_zkmax);
}

exports['build:single'] = typescript_build_single;
exports.watch = gulp.series(
    browsersync_init,
    gulp.parallel(
        watch_typescript_zul,
        watch_typescript_zkex,
        watch_typescript_zkmax,
    )
);
exports.build = gulp.parallel(
    typescript_build_zul,
    typescript_build_zkex,
    typescript_build_zkmax,
);
exports.default = exports.build;
