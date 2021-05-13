var binder = zkbind.$('$root');
function hello(){
    binder.command('hello', {data: 'value'});
}
var count = 0;
binder.after('notifyClient', function () {
	jq('#span').html('after command "notifyClient" (' + count++ + ')');
});