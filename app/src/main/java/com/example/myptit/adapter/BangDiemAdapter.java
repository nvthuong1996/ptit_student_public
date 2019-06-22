package com.example.myptit.adapter;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.myptit.model.HocPhanModel;
import com.example.myptit.R;
import java.util.List;

public class BangDiemAdapter extends ArrayAdapter<HocPhanModel> {
    private Activity context;
    private List<HocPhanModel> objects;
    private int resource;

    public BangDiemAdapter(Activity context, int resource, List<HocPhanModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = this.context.getLayoutInflater().inflate(this.resource, null);
        TextView txtSoTinChi = (TextView) row.findViewById(R.id.txtSoTinChi_item);
        TextView txtDiemChu = (TextView) row.findViewById(R.id.txtDiemChu_DuKien_item);
        TextView txtDiemQuaTrinh = (TextView) row.findViewById(R.id.txtDiemQuaTrinh);
        TextView txtDiemCuoiKi = (TextView) row.findViewById(R.id.txtDiemCuoiKi);
        HocPhanModel diemHocPhanModel = (HocPhanModel) this.objects.get(position);
        ((TextView) row.findViewById(R.id.txtTenHocPhan_DuKien_item)).setText(diemHocPhanModel.getTenHocPhan());
        txtSoTinChi.setText(diemHocPhanModel.getSoTinChi());
        txtDiemChu.setText(diemHocPhanModel.getDiemChu());
        txtDiemQuaTrinh.setText(diemHocPhanModel.getDiemQuaTrinh());
        txtDiemCuoiKi.setText(diemHocPhanModel.getDiemCuoiKi());
        return row;
    }
}
