import _hyperscript from "hyperscript.org";

_hyperscript.browserInit();

document.addEventListener('sse:todo.listed', function (evt) {
    const params = evt.detail.data;
    const url = params
        ? `/todo?${params}`
        : '/todo'

    htmx.ajax('GET', url, {
        target: '#todo-grid-loader',
        swap: 'outerHTML'
    });
});