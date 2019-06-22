const request = require("request");
const fs = require("fs");
const HEADER = JSON.parse(fs.readFileSync("./otherData/header.json", "utf8"));
const cheerio = require("cheerio");

module.exports = crawlerLichThi;

function requestLichThi(session){
    const header = JSON.parse(JSON.stringify(HEADER));
    header["Cookie"] = session;
    return new Promise((resolve,reject)=>{
        request({
            uri:'http://qldt.ptit.edu.vn/Default.aspx?page=xemlichthi',
            headers:header,
            method:'GET'
        },(error,req,body)=>{
            if(error||req.statusCode!==200){
                return reject(new Error("error crawler lich thi"));
            }
            return resolve(body);
        })
    })
}

async function crawlerLichThi(session){
    const body =await requestLichThi(session);
    const $ = cheerio.load(body);
    const table = $("#ctl00_ContentPlaceHolder1_ctl00_gvXem");
    if(table.length!==1){
        return {error:new Error("Error crawler lich thi")};
    }
    const tbody = table[0].children.find(item=>item.name==='tbody'&&item.type==='tag').children.filter(item=>item.type==='tag');
    const result = tbody.map((e)=>{
       return e.children.filter(item=>item.type==='tag').map((e)=>{
         return $(e).text().trim();
       }) 
    })
    const r = result.slice(1);
    return r;
}