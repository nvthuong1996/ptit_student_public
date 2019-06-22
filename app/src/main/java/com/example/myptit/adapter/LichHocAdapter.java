package com.example.myptit.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.myptit.R;
import com.example.myptit.TKBActivity;
import com.example.myptit.model.LichHocModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LichHocAdapter extends ArrayAdapter<LichHocModel> {
    private Activity context;
    private List<LichHocModel> objects;
    private  int tuan;

    public LichHocAdapter(Activity context, int resource, List<LichHocModel> objects,int tuan) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
        this.tuan = tuan;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = this.context.getLayoutInflater().inflate(R.layout.item_lichhoc, null);
        TextView txtTenLop_item = (TextView) row.findViewById(R.id.txtTenLop_item);
        TextView txtPhongHoc_item = (TextView) row.findViewById(R.id.txtPhongHoc_item);
        TextView textViewTuan = (TextView) row.findViewById(R.id.textViewTuan);
        final LichHocModel lichHocModel = (LichHocModel) this.objects.get(position);
        ((TextView) row.findViewById(R.id.txtThoiGian_item)).setText(lichHocModel.gethBatDau());
        txtTenLop_item.setText(lichHocModel.getTenMh());
        txtPhongHoc_item.setText(lichHocModel.getPHONG());

        int tuanthu = this.tuan;



        SpannableString str = new SpannableString(lichHocModel.getTUAN());

        if(tuanthu > 0 && tuanthu <= lichHocModel.getTUAN().length()){
            str.setSpan(new BackgroundColorSpan(Color.YELLOW), tuanthu - 1, tuanthu, 0);
            textViewTuan.setText(str);
        }else{
            textViewTuan.setText(str);
        }

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.tkb_detail);

                TextView giangvienTxt = (TextView)dialog.findViewById(R.id.textView26);
                giangvienTxt.setText(lichHocModel.getGV());

                TextView nhomTxt = (TextView)dialog.findViewById(R.id.textView27);
                nhomTxt.setText(lichHocModel.getNhomToThucHanh());

                TextView sotinchiTxt = (TextView)dialog.findViewById(R.id.textView33);
                sotinchiTxt.setText(lichHocModel.getSTC());

                TextView msmhTxt = (TextView)dialog.findViewById(R.id.textView34);
                msmhTxt.setText(lichHocModel.getMaMH());

                TextView tenmhTxt = (TextView)dialog.findViewById(R.id.textView35);
                tenmhTxt.setText(lichHocModel.getTenMh());

                dialog.show();

            }
        });

        return row;
    }
}
