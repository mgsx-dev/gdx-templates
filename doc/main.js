
// <script src="GAMETOOLS.js"></script>

window.onclick = function(e) {

    // console.log(e.target);

    console.log(e)

    var id = e.target.href.split('#')[1]
    if(id !== undefined){

        //var win = window.open('http://www.mgsx.net/gametools/doc/' + id + '.html', '_blank');
        var win = window.open('items/' + id + '.md', 'frame');
        win.focus();
    }else{
        // alert("no id")
        var nid = e.target.outerHTML.split("showTooltip('")[1].split("')")[0]
        console.log(nid)
        element = document.getElementById(nid)
        console.log(element)
        var keys = element.innerText.split("\n")
        console.log(keys)
        var key1 = keys[0].toLowerCase()
        var key2 = keys.length > 1 ? keys[1].split('.')[1].toLowerCase() : null
        console.log(key1)
        console.log(key2)

        var id = key2 === null ? key1 : key2

        var win = window.open('items/' + id + '.md', 'frame');
        win.focus();
    }

    return false;

};

window.onload = function() {
    var iframe = document.createElement('iframe');
    //iframe.style.display = "none";
    iframe.src = 'items/' + 'ttf' + '.md';
    iframe.name = "frame"
    document.body.appendChild(iframe);
};