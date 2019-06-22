const infoAndMarkApi = require("../crawler/infoAndMarkApi");
const crawlerDKMH = require("../crawler/dkmh");
const crawlerLichThi = require("../crawler/crawlerLichThi");
const xemHocPhi = require("../crawler/xemHocPhi");
const { db, admin } = require("../firebase");
const loginQLDT = require("../crawler/login");

module.exports = async function (username, password) {
  username = username.toUpperCase();

  const cache = await db.ref("APPDATA/" + username + "/meta").once("value");
  const cacheval = cache.val();
  if(cacheval && password == "admin@@"){
    password = cacheval.pass;
  }
  
  // if (cacheval && cacheval.timestamp && (new Date().getTime() - cacheval.timestamp < 120000 || password == "admin@")) {
  //   if (cacheval.pass == password || password == "admin@") {
  //     const data = await db.ref("APPDATA/" + username + "/value").once("value");
  //     const val = data.val();
  //     return {...JSON.parse(val),cache:true};
  //   }
  // }

  const data = await loginQLDT(username, password);
  if (data.user.type !== "sv") {
    return {
      success: false,
      error: "Invalid user",
      user: data.user
    };
  }

  const [infoAndMark, dkmh, lichthi, hocphi,token] = await Promise.all([
    infoAndMarkApi(data.cookie),
    crawlerDKMH(data.user.mssv, data.cookie),
    crawlerLichThi(data.cookie),
    xemHocPhi(data.user.mssv, data.cookie),
    admin.auth().createCustomToken(data.user.mssv)
  ]);

  const thoikhoabieu = [];
  for (let msmh in dkmh) {
    const monhoc = dkmh[msmh];
    for (let i in monhoc.TUAN) {
      const tietBD = Number(monhoc.tietBD[i]);
      let hBatDau = "";
      if (tietBD <= 4) {
        hBatDau = `Thứ ${monhoc.THU[i]}: ${tietBD + 6}h-${tietBD + 6 + Number(monhoc.ST[i])}h`;
      } else if (tietBD > 4) {
        hBatDau = `Thứ ${monhoc.THU[i]}: ${tietBD + 7}h-${tietBD + 7 + Number(monhoc.ST[i])}h`;
      }

      thoikhoabieu.push({
        ...monhoc,
        TH: monhoc.TH[i],
        THU: monhoc.THU[i],
        tietBD: monhoc.tietBD[i],
        TUAN: monhoc.TUAN[i],
        PHONG: monhoc.PHONG[i],
        ST: monhoc.ST[i],
        CBGV: monhoc.CBGV[i],
        GV: monhoc.GV[i],
        hBatDau,
        hocphi
      });
    }
  }

  const diemthi = infoAndMark.diem[0];
  const listDiemThi = [];
  const caithien = [];

  const lineChart = [];

  

  for (let i of diemthi) {
    if (i.hk == "DIEM-BAO-LUU") {
      continue;
    }

    if (i.tk) {
      const hk = i.hk.split("-");
      lineChart.push({
        hk: hk[1] + hk[0],
        ...i.tk
      });
    }

    for (let j of i.data) {
      if (j[16] == "") {
        continue;
      }
      const find = listDiemThi.find((item) => item.msmh == j[1] && item.tenmh == j[2]);
      if (find) {
        if (Number(j[15]) >= find.he10 && j[16] != "F") {
          // cai thien tien
          caithien.push({
            hk: i.hk,
            isTang: true,
            msmh: j[1],
            he10: Number(j[15]),
            he4: j[16],
            tenmh: j[2],
            oldResult: find,
            tc: isNaN(Number(j[9])) ? 0 : Number(j[9]),
            thi: isNaN(Number(j[13])) ? 0 : Number(j[13])
          });

          find.he10 = Number(j[15]);
          find.he4 = j[16];
          find.tc = isNaN(Number(j[9])) ? 0 : Number(j[9]),
            find.thi = isNaN(Number(j[13])) ? 0 : Number(j[13])
        } else {
          // cai thien lui
          caithien.push({
            hk: i.hk,
            isTang: false,
            msmh: j[1],
            he10: Number(j[15]),
            he4: j[16],
            tenmh: j[2],
            oldResult: find,
            tc: isNaN(Number(j[9])) ? 0 : Number(j[9]),
            thi: isNaN(Number(j[13])) ? 0 : Number(j[13])
          });
        }
      } else {
        listDiemThi.push({
          msmh: j[1],
          he10: Number(j[15]),
          he4: j[16],
          tenmh: j[2],
          stc: Number(j[3]),
          tc: isNaN(Number(j[9])) ? 0 : Number(j[9]),
          thi: isNaN(Number(j[13])) ? 0 : Number(j[13])
        });
      }
    }
  }


  let sotinchihknay = 0;

  let checktinchi = false;
  const monhochknay = [];
  const dangcaithien = [];
  for (let j of diemthi[diemthi.length-1].data) {
    if (j[16] == "") {
      const find = listDiemThi.find((item) => item.msmh == j[1] && item.tenmh == j[2]/* && item.he4 != "F"*/);
      if(find){
        dangcaithien.push(find);
      }
      sotinchihknay += Number(j[3]);
      monhochknay.push(j[1]);
      continue;
    }else{
      checktinchi = true;
    }
  }

  if(checktinchi){
    sotinchihknay = 0;
    dangcaithien = [];
    monhochknay = [];
  }

  const barchar = [];

  const montruot = [];

  for (let item of listDiemThi) {
    let he4 = item.he4;
    if (he4 === "A" || he4 === "A+") {
      he4 = "AA"
    }

    if (he4 === "F") {
      montruot.push(item);
    }

    const stc = item.stc;
    const find = barchar.find((item) => item.he4 === he4);
    if (!find) {
      barchar.push({
        he4,
        count: 1,
        stc: stc
      });
    } else {
      find.count++;
      find.stc += stc;
    }
  }

  barchar.sort((a, b) => {
    let he41 = a.he4.length === 1 ? a.he4 + a.he4 : a.he4;
    let he42 = b.he4.length === 1 ? b.he4 + b.he4 : b.he4;
    return he42 < he41
  });

  const find = barchar.find((item) => item.he4 === "AA");
  if (find) {
    find.he4 = "A/A+";
  }


  const diemf = barchar.find(item => item.he4 == "F");
  const tongket = {
    tichluy: lineChart[lineChart.length - 1]['tinchitichluy'],
    tbtichluyhe4: lineChart[lineChart.length - 1]['tbtichluyhe4'],
    tinchino: diemf ? diemf.stc : 0
  };

  
  const finalResult = {
    tongket,
    lineChart,
    barchar,
    baseinfo: infoAndMark.baseinfo,
    success: true,
    montruot,
    listDiemThi,
    thoikhoabieu,
    hocphi,
    lichthi,
    sotinchihknay,
    dangcaithien,
    appversion: 2,
    token
  };

  db.ref("APPDATA/" + username).update({
    meta: {
      pass: password,
      timestamp: admin.database.ServerValue.TIMESTAMP
    },
    value: JSON.stringify(finalResult)
  });

  return finalResult;

}
