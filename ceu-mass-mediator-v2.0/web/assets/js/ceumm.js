/*
	JavaScript Library for Ceu Mass mediator
        Author: Alberto Gil de la Fuente
*/



function getmailaddress(dom,subdom,dir) {
    var email = dir;
    email+="@";
    email+=subdom;
    email+=".";
    email+=dom;
    document.write("<a href='");
    document.write("mai"+"lto");
    document.write(":");
    document.write(email);
    document.write("'>");
    document.write(email);
    document.write("</a>");
    }
