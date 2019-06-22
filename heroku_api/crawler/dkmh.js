const request = require("request");
const fs = require("fs");
const cheerio = require("cheerio");
const HEADER = JSON.parse(fs.readFileSync("./otherData/header.json", "utf8"));
const cbgvmap = require("../CBGV.json");

// crawlerDKMH("B14DCCN762");

module.exports = crawlerDKMH;

async function crawlerDKMH(mssv, cookie) {
  //cookie = "ASP.NET_SessionId=wnvrsr55vpwmwu55wiyzudbb";

  const tkb = await crawlerTKB(mssv, cookie);

  if (tkb.error){
      return tkb;
  }
  const result = {};
  tkb.forEach(item => {
    result[item.maMH] = item;
  });

  for(let i in result){
    let item = result[i];
    item['GV'] = [];
    for(let i in item.CBGV){
      let mcb = item.CBGV[i];
      if(!cbgvmap[mcb]){
        const r = await getNameGV(mcb, cookie);
        if(r.err){
          item['GV'][i] = "";
        }else{
          item['GV'][i] = r.name;
          cbgvmap[mcb] = r.name;
        }
      }else{
        item['GV'][i] = cbgvmap[mcb];
      }
    }
  }

  fs.writeFileSync("./CBGV.json",JSON.stringify(cbgvmap));
  return result;
}

async function crawlerTKB(msgv, cookie) {
  const req = await requestHTML(msgv, cookie);
  if (req.err) {
    //handle err
    console.log("có lỗi khi request: " + msgv);
    return null;
  }
  const $ = cheerio.load(req.body.replace(/\n\r/g, ""), {
    decodeEntities: false
  });
  const result = [];
  const arr = $(".grid-roll2 > table");

  let label = $($("#ctl00_ContentPlaceHolder1_ctl00_lblNote")).text();
  label = label.replace(/\)/g,"");
  const _ngaybatdau = label.split(" ");
  const ngaybatdau = _ngaybatdau[_ngaybatdau.length - 1];


  if (arr.length === 0) return {error:new Error("Lỗi phân tích thời khóa biểu"),body:req.body};
  for (let i = 0; i < arr.length; i++) {
    const item = arr[i];
    const q = item.children
      .find(item => item.type === "tag" && item.name === "tbody")
      .children.find(item => item.type === "tag" && item.name === "tr")
      .children.filter(item => item.type === "tag" && item.name === "td");

    let k = [];
    for (let i = 7; i < q.length; i++) {
      if (i === q.length - 1) {
        const _k = /href="(.*?)"/.exec($(q[i]).html());
        if (_k) {
          k.push(_k[1].trim());
        }
        continue;
      }
      const _tmp = q[i].children.filter(item => item.type === "tag").map(e => {
        return $(e).text();
      });

      k.push(_tmp);
    }

    const obj = {
      maMH: $(q[0]).text(),
      tenMh: $(q[1]).text(),
      NMH: $(q[2]).text(),
      STC: $(q[3]).text(),
      maLop: $(q[4]).text(),
      STCHP: $(q[5]).text(),
      KDK: "",
      TH: k[0],
      THU: k[1],
      tietBD: k[2],
      ST: k[3],
      PHONG: k[4],
      CBGV: k[5],
      TUAN: k[6],
      DSSV: k[7],
      ngaybatdau
    };
    result.push(obj);
  }
  return result;
}

function requestHTML(msgv, cookie) {
  if (!msgv) return Promise.resolve({ err: "msgv undefined" });
  var headers = JSON.parse(JSON.stringify(HEADER));
  headers["Cookie"] = cookie;
  return new Promise((resolve, reject) => {
    request(
      {
        method: "GET",
        uri: "http://qldt.ptit.edu.vn/default.aspx?page=thoikhoabieu&sta=1&id="+msgv,
        headers
      },
      (err, res, body) => {
        if (err || res.statusCode !== 200) {
          reject(new Error({ err: true }));
        } else {
          resolve({ body });
        }
      }
    );
  });
}



function getNameGV(msgv, cookie) {
  if (!msgv) return Promise.resolve({ err: "msgv undefined" });
  var headers = JSON.parse(JSON.stringify(HEADER));
  headers["Cookie"] = cookie;
  return new Promise((resolve, reject) => {
    request(
      {
        method: "GET",
        uri: "http://qldt.ptit.edu.vn/default.aspx?page=thoikhoabieu&sta=1&id="+msgv,
        headers
      },
      (err, res, body) => {
        if (err || res.statusCode !== 200) {
          reject(new Error({ err: true }));

        } else {
            const $ = cheerio.load(body.replace(/\n\r/g, ""), {
              decodeEntities: false
            });
            const gve = $("#ctl00_ContentPlaceHolder1_ctl00_lblContentTenSV");
            const tengv = $(gve).text();
            return resolve({name:tengv});
        }
      }
    );
  });
}

