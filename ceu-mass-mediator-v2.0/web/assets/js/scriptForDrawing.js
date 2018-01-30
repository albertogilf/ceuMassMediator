var canvas = document.getElementById('pentagon');
var ctx = canvas.getContext('2d');

drawPentagon();

function drawPentagon()
{
    var poly = [100, 0, 200, 0, 250, 50, 150,100, 50,50];
    ctx.fillStyle = '#f00';
    
    ctx.translate(ctx.canvas.clientWidth/2,ctx.canvas.clientHeight);
    //var myWindow = window.open('','','width=200,height=100')
    //myWindow.document.write("widh is ")
    //myWindow.document.write(ctx.canvas.clientWidth)
    
    ctx.beginPath();
    ctx.moveTo(poly[0], poly[1]);
    for (item = 2; item < poly.length - 1; item += 2) {
        ctx.lineTo(poly[item], poly[item + 1]);
    }

    ctx.closePath();
    ctx.fill();
}