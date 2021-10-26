

async function checkRedirect(){
    const  response = await fetch('login', {method: 'post',});
    if(response.redirected){
        console.log(response.url);
        let splitStr = response.url.split("/");
        let responseUri = splitStr[splitStr.length - 1];

        if(responseUri === "login.html") window.stop();
        else window.location.href = response.url;
    }
}

checkRedirect();