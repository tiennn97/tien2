package com.tiennn.appbandoanvanphong.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tiennn.appbandoanvanphong.R;
import com.tiennn.appbandoanvanphong.model.Sanpham;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MonAnKhongThichAdapter extends BaseAdapter {
    Context context;
    ArrayList<Sanpham> arraykhongthich;

    public MonAnKhongThichAdapter(Context context, ArrayList<Sanpham> arraykhongthich) {
        this.context = context;
        this.arraykhongthich = arraykhongthich;
    }

    @Override
    public int getCount() {
        return arraykhongthich.size();
    }

    @Override
    public Object getItem(int i) {
        return arraykhongthich.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class ViewHolder{
        public TextView txttenkhongthich,txtgiakhongthich,txtmotakhongthich;
        public ImageView imgkhongthich;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (viewHolder == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dong_khongthich,null);
            viewHolder.txttenkhongthich = view.findViewById(R.id.textviewtenkhongthich);
            viewHolder.txtgiakhongthich = view.findViewById(R.id.textviewgiakhongthich);
            viewHolder.txtmotakhongthich = view.findViewById(R.id.textviewmotakhongthich);
            viewHolder.imgkhongthich = view.findViewById(R.id.imageviewkhongthich);

            view.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Sanpham sanpham = (Sanpham) getItem(i);
        viewHolder.txttenkhongthich.setText(sanpham.getTensanpham());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        viewHolder.txtgiakhongthich.setText("Giá : " + decimalFormat.format(sanpham.getGiasanpham())+ "Đ");
        viewHolder.txtmotakhongthich.setMaxLines(2);
        viewHolder.txtmotakhongthich.setEllipsize(TextUtils.TruncateAt.END);
        viewHolder.txtmotakhongthich.setText(sanpham.getMotasanpham());
        Picasso.with(context).load(sanpham.getHinhanhsanpham())
                .placeholder(R.drawable.wait)
                .error(R.drawable.error)
                .into(viewHolder.imgkhongthich);
        return view;
    }
}
