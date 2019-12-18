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

public class MonAnUaThichAdapter extends BaseAdapter {
    Context context;
    ArrayList<Sanpham> arrayuathich;

    public MonAnUaThichAdapter(Context context, ArrayList<Sanpham> arrayuathich) {
        this.context = context;
        this.arrayuathich = arrayuathich;
    }

    @Override
    public int getCount() {
        return arrayuathich.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayuathich.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class ViewHolder{
        public TextView txttenuathich,txtgiauathich,txtmotauathich;
        public ImageView imguathich;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (viewHolder == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dong_uathich,null);
            viewHolder.txttenuathich = view.findViewById(R.id.textviewtenuathich);
            viewHolder.txtgiauathich = view.findViewById(R.id.textviewgiauathich);
            viewHolder.txtmotauathich = view.findViewById(R.id.textviewmotauathich);
            viewHolder.imguathich = view.findViewById(R.id.imageviewuathich);

            view.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Sanpham sanpham = (Sanpham) getItem(i);
        viewHolder.txttenuathich.setText(sanpham.getTensanpham());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        viewHolder.txtgiauathich.setText("Giá : " + decimalFormat.format(sanpham.getGiasanpham())+ "Đ");
        viewHolder.txtmotauathich.setMaxLines(2);
        viewHolder.txtmotauathich.setEllipsize(TextUtils.TruncateAt.END);
        viewHolder.txtmotauathich.setText(sanpham.getMotasanpham());
        Picasso.with(context).load(sanpham.getHinhanhsanpham())
                .placeholder(R.drawable.wait)
                .error(R.drawable.error)
                .into(viewHolder.imguathich);
        return view;
    }
}
