const request = require("request");
const fs = require("fs");
const cheerio = require("cheerio");
const HEADER = JSON.parse(fs.readFileSync("./otherData/header.json", "utf8"));

//crawAndUpdate("B14DCCN762");

module.exports = crawlerTKB;


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

  const tongsotinchi = $($("#ctl00_ContentPlaceHolder1_ctl00_SoTinChiHP")).text().trim();
  const tongsotinchihp = $($("#ctl00_ContentPlaceHolder1_ctl00_SoTinChi")).text().trim();
  const tongso1 = $($("#ctl00_ContentPlaceHolder1_ctl00_lblphaiDong")).text().trim();
  const tongso2 = $($("#ctl00_ContentPlaceHolder1_ctl00_lblDongLanDau1")).text().trim();
  const sotiendadong = $($("#ctl00_ContentPlaceHolder1_ctl00_lblDaDongHKOffline")).text().trim();
  const sotienconno = $($("#ctl00_ContentPlaceHolder1_ctl00_lblConNoHocKy")).text().trim();

  return {
    tongsotinchi,
    tongsotinchihp,
    tongso1,
    tongso2,
    sotiendadong,
    sotienconno
  };
}

function requestHTML(msgv, cookie) {
  if (!msgv) return Promise.resolve({ err: "msgv undefined" });
  var headers = JSON.parse(JSON.stringify(HEADER));
  headers["Cookie"] = cookie;
  return new Promise((resolve, reject) => {
    request(
      {
        method: "GET",
        uri: "http://qldt.ptit.edu.vn/Default.aspx?page=xemhocphi",
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
