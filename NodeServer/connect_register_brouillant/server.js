let http =require('http')
let fs= require('fs')
let url=require('url')

let server =http.createServer()
server.on('request',(request,response)=>{

    fs.readFile('index.html',(err,data)=>{
        if (err) {
            response.writeHead(404)
            response.end('page demander nexiste pas')
        }
        else {
            response.writeHead(200)
            let query= (url.parse(request.url,true)).query
            
            response.end('bonjour '+query.name)
        }
})
})
server.listen(8000)