const request = require("request");
const fs = require("fs");
const HEADER = JSON.parse(fs.readFileSync("./otherData/header.json", "utf8"));
const md5 = require("md5");
const cheerio = require("cheerio");
const cbgvmap = require("../CBGV.json");


//crawAndUpdate("B14DCCN762");

module.exports = crawlerDKMH;

async function crawlerDKMH (mssv){
    const tkb= await crawlerTKB(mssv);

    if(!tkb) return null;

    return tkb.map((e)=>{
      return [e.maMH+e.NMH,e.tenMh]
    })
}


async function crawlerTKB(msgv){
    const req = await requestHTML(msgv);
    if(req.err){
		//handle err
		console.log("có lỗi khi request: "+msgv);
        return null;
    }
    const $ = cheerio.load(req.body.replace(/\n\r/g,""),{
		decodeEntities:false
	});
	const result = [];
    const arr = $(".grid-roll2 > table");
    


	if(arr.length===0) return null;
	for(let i=0;i<arr.length;i++){
		const item = arr[i];
		const q = item.children.find(item=>item.type==="tag"&&item.name==="tbody")
			.children.find(item=>item.type==="tag"&&item.name==="tr")
            .children.filter(item=>item.type==="tag"&&item.name==="td");

		let k =[];
		for(let i=7;i<q.length;i++){

            if(i===q.length-1){
                const _k = /href="(.*?)"/.exec($(q[i]).html());
                if(_k){
                  k.push(_k[1].trim());
                }
                continue;
            }
	      const _tmp = q[i].children.filter(item=> item.type==="tag").map((e)=>{
                return $(e).text()
            });

			      k.push(_tmp);
        }

        let tengv = "";
        
        if(!cbgvmap[k[5]]){

        }else{
            tengv = cbgvmap[k[5]];
        }

		const obj ={
			maMH:$(q[0]).text(),
			tenMh:$(q[1]).text(),
			NMH:$(q[2]).text(),
			STC:$(q[3]).text(),
			maLop:$(q[4]).text(),
			STCHP:$(q[5]).text(),
			KDK:"",
			TH:k[0],
			THU:k[1],
			tietBD:k[2],
			ST:k[3],
			PHONG:k[4],
			CBGV:k[5],
			TUAN:k[6],
			DSSV:k[7]
		}
		result.push(obj);
	}
	return result;
}

function requestHTML(msgv,cookie){
    if(!msgv) return Promise.resolve({err:"msgv undefined"});
    var header = JSON.parse(JSON.stringify(HEADER));
    header["Cookie"] = cookie;
    header["Content-Type"] = "application/x-www-form-urlencoded";
    return new Promise((resolve,reject)=>{
        request({
            uri:"http://qldt.ptit.edu.vn/default.aspx?page=thoikhoabieu&sta=1",
            header
        },(err,res,body)=>{
            if(err||res.statusCode!==200){
                reject(new Error({err:true}));
            }else{
                resolve({body});
            }
        })
    });
}
