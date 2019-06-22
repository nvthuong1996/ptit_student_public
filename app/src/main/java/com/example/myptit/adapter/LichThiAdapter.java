package com.example.myptit.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.myptit.R;
import com.example.myptit.model.LichThiModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LichThiAdapter extends ArrayAdapter<LichThiModel> {
    private Activity context;
    private List<LichThiModel> objects;
    private int resource;

    public LichThiAdapter(Activity context, int resource, List<LichThiModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
        this.resource = resource;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = this.context.getLayoutInflater().inflate(this.resource, null);
        LichThiModel lichthi = (LichThiModel) this.objects.get(position);
        TextView txtTenLop_item = (TextView) row.findViewById(R.id.txtTenLop_item);

        TextView thoigian = (TextView) row.findViewById(R.id.txtThoiGian_item_);
        TextView tenmonhoc = (TextView) row.findViewById(R.id.txtTenLop_item_);
        TextView phong = (TextView) row.findViewById(R.id.txtPhongHoc_item_);
        TextView sophut = (TextView) row.findViewById(R.id.textViewTuan_);


        thoigian.setText(lichthi.getNgaythi()+" "+lichthi.getGiobatdau());
        tenmonhoc.setText(lichthi.getTenmonhoc());
        phong.setText(lichthi.getPhong());
        sophut.setText(lichthi.getSophut()+" phút" + "("+lichthi.getGhichu()+")");

        return row;
    }
}
