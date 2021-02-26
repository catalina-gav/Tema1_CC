async function postData(url = '') {
    // Default options are marked with *
    const response = await fetch('http://localhost:8000/api/cycle', {
        method: 'POST', // *GET, POST, PUT, DELETE, etc.
        mode: 'no-cors', // no-cors, *cors, same-origin
        cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
        credentials: 'same-origin', // include, *same-origin, omit
        headers: {
            'Content-Type': 'application/json'
            // 'Content-Type': 'application/x-www-form-urlencoded',
        },

    });
    return response; // parses JSON response into native JavaScript objects
}

function runApp()
{

    fetch('http://localhost:8000/api/cycle')
        .then(function(response) {
            console.log(response)
            return response.json();

        })
        .then(function(myJson) {
            console.log('Request successful', myJson);

            console.log(myJson.items.length);
            console.log(myJson.items[0].id.videoId);
            var i;
            var leftDiv = document.createElement("div"); //Create left div
            leftDiv.id = "left"; //Assign div id
            leftDiv.setAttribute("style", "float:left; width:66.5%; line-height: 26px; text-align:left; font-size:12pt; padding-left:8px; height:26px;"); //Set div attributes
            //leftDiv.style.background =  "#FF0000";

            p = document.createElement('p');
            p.textContent="Aici aveti linkurile catre clipurile de pe youtube trimise de api-ul nostru";
            leftDiv.appendChild(p); // Append the link to the div
            for (i = 0; i < myJson.items.length; i++) {

                a = document.createElement('a');
                p = document.createElement('p');
                var url="https://www.youtube.com/watch?v="+myJson.items[i].id.videoId;
                a.href =  url;
                a.target="_blank"
                a.innerHTML = myJson.items[i].snippet.title
                br = document.createElement('br');
                leftDiv.appendChild(a); // Append the link to the div
                leftDiv.appendChild(br); // Append the link to the div

            }
            document.body.appendChild(leftDiv); // And append the div to the document body


        })
        .catch(function(error) {
            console.log('Request failed', error)
        });


}
function concurrentCalls()
{

    fetch('http://localhost:8000/api/parallel')
        .then(function(response) {
            console.log(response)
            return response.text();

        })
        .then(function(myJson) {
            console.log('Request successful', myJson);
        })
        .catch(function(error) {
            console.log('Request failed', error)
        });

}
function getMetrics()
{

    fetch('http://localhost:8000/api/metrics')
        .then(function(response) {
            console.log(response)
            return response.json();

        })
        .then(function(myJson) {

            var rightDiv = document.createElement("div"); //Create left div
            rightDiv.id = "right"; //Assign div id
            rightDiv.setAttribute("style", "float:right; width:66.5%; line-height: 26px; text-align:left; font-size:12pt; padding-left:8px; height:26px;"); //Set div attributes
            //leftDiv.style.background =  "#FF0000";

            p = document.createElement('p');
            p.textContent="Location Api calls :" + myJson.loc_calls;
            rightDiv.appendChild(p); // Append the link to the div
            p = document.createElement('p');
            p.textContent="Location Api average latency :" + myJson.loc_lat;
            rightDiv.appendChild(p); // Append the link to the div
            p = document.createElement('p');
            p.textContent="Random Api average latency :" + myJson.rand_lat;
            rightDiv.appendChild(p); // Append the link to the div
            p = document.createElement('p');
            p.textContent="Youtube Api average latency :" + myJson.yt_lat;
            rightDiv.appendChild(p); // Append the link to the div
            p = document.createElement('p');
            document.body.appendChild(rightDiv); // And append the div to the document body
            console.log('Request successful', myJson);
        })
        .catch(function(error) {
            console.log('Request failed', error)
        });

}