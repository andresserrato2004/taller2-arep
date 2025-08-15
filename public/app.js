function loadGetMsg() {
  const nameVar = document.getElementById("name").value;
  const xhttp = new XMLHttpRequest();
  xhttp.onload = function() {
    document.getElementById("getrespmsg").innerText = this.responseText;
  };
  xhttp.open("GET", "/hello?name=" + encodeURIComponent(nameVar));
  xhttp.send();
}
// POST using fetch, passing name in query as per assignment help
function loadPostMsg(inp) {
  const nameVal = inp && inp.value ? inp.value : document.getElementById("postname").value;
  const url = "/hellopost?name=" + encodeURIComponent(nameVal);
  fetch(url, { method: "POST" })
    .then((res) => res.text())
    .then((txt) => {
      document.getElementById("postrespmsg").innerText = txt;
    })
    .catch((err) => {
      document.getElementById("postrespmsg").innerText = "Error: " + err;
    });
}
