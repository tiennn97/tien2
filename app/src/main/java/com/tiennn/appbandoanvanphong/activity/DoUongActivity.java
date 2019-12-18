package com.tiennn.appbandoanvanphong.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tiennn.appbandoanvanphong.R;
import com.tiennn.appbandoanvanphong.adapter.DoAnAdapter;
import com.tiennn.appbandoanvanphong.adapter.DoUongAdapter;
import com.tiennn.appbandoanvanphong.model.Giohang;
import com.tiennn.appbandoanvanphong.model.Sanpham;
import com.tiennn.appbandoanvanphong.ultit.CheckConnection;
import com.tiennn.appbandoanvanphong.ultit.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoUongActivity extends AppCompatActivity {

    Toolbar toolbardouong;
    ListView lvdouong;
    DoUongAdapter doUongAdapter;
    ArrayList<Sanpham> mangdouong;

    int iddouong = 0;
    int page = 1;

    View footerview;
    boolean isLoading = false;
    boolean limitdata = false;

    mHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_uong);

        Anhxa();
        GetIdloaisp();
        ActionToolbar();
        GetData(page);
        LoadMoreData();

        registerForContextMenu(lvdouong); // khai báo context menu
    }

    private void Anhxa() {
        toolbardouong = findViewById(R.id.toolbardouong);
        lvdouong = findViewById(R.id.listviewdouong);
        mangdouong = new ArrayList<>();
        doUongAdapter = new DoUongAdapter(getApplicationContext(),mangdouong);
        lvdouong.setAdapter(doUongAdapter);

        // gắn layout progressbar
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        footerview = inflater.inflate(R.layout.progressbar,null);

        mHandler = new mHandler();
    }

    private void GetIdloaisp() {
        iddouong = getIntent().getIntExtra("idloaisanpham",-1);
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbardouong);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbardouong.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void GetData(int Page) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String duongdan = Server.Duongdandoan+String.valueOf(Page);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, duongdan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int id = 0;
                String Tendouong = "";
                int Giadouong = 0;
                String Hinhanhdouong = "";
                String Motadouong = "";
                int Idspdouong = 0;

                if (response != null && response.length() != 2){ //response luôn trả về {} -> luôn có 2 phần tử
                    lvdouong.removeFooterView(footerview);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0 ; i <jsonArray.length() ; i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            id = jsonObject.getInt("id");
                            Tendouong = jsonObject.getString("tensp");
                            Giadouong = jsonObject.getInt("giasp");
                            Hinhanhdouong = jsonObject.getString("hinhanhsp");
                            Motadouong = jsonObject.getString("motasp");
                            Idspdouong = jsonObject.getInt("idsanpham");

                            mangdouong.add(new Sanpham(id , Tendouong , Giadouong , Hinhanhdouong , Motadouong , Idspdouong));
                            doUongAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    limitdata = true;
                    lvdouong.removeFooterView(footerview);
                    CheckConnection.ShowToast_Short(getApplicationContext(),"Het");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<String,String>();
                param.put("idsanpham",String.valueOf(iddouong));
                return param;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void LoadMoreData() {
        lvdouong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),ChiTietSanPhamActivity.class);
                intent.putExtra("thongtinsanpham",mangdouong.get(i));
                startActivity(intent);
            }
        });

        lvdouong.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int FirstItem, int VisibleItem, int TotalItem) {
                if (FirstItem + VisibleItem == TotalItem && TotalItem != 0 && isLoading == false && limitdata == false){
                    isLoading = true;
                    ThreadData threadData = new ThreadData();
                    threadData.start();
                }
            }
        });
    }

    public class mHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    lvdouong.addFooterView(footerview);
                    break;
                case 1:
                    GetData(++page);
                    isLoading = false;
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public class ThreadData extends Thread{
        @Override
        public void run() {
            mHandler.sendEmptyMessage(0);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message message = mHandler.obtainMessage(1); //obtainMessage de lien ket Thread voi handler
            mHandler.sendMessage(message);
            super.run();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menugiohang:
                Intent intent = new Intent(getApplicationContext(),GiohangActivity.class);
                startActivity(intent);
                break;
            case R.id.menutimkiem:
                Intent intent1 = new Intent(getApplicationContext(),TimKiemActivity.class);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_context, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Sanpham f = mangdouong.get(info.position);

        menu.setHeaderTitle(f.getTensanpham());


        super.onCreateContextMenu(menu, v, menuInfo);
    }

    /**
     * Xử lý lựa chọn trên CONTEXT MENU
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int idMenu = item.getItemId();

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Sanpham f = mangdouong.get(info.position);

        final String t = f.getTensanpham();
        final String h = f.getHinhanhsanpham();
        final int iddd = f.getID();
        final int giaa = f.getGiasanpham();
        final int loaisp = f.getIDSanpham();
        final String motasp = f.getMotasanpham();


        switch (idMenu){
            case R.id.menu_themvaogiohang:



                if (MainActivity.manggiohang.size() > 0){
                    int sl = 1;
                    boolean exists = false;
                    for (int i = 0 ; i < MainActivity.manggiohang.size() ; i++){
                        if (MainActivity.manggiohang.get(i).getIdsp() == iddd){
                            MainActivity.manggiohang.get(i).setSoluongsp(MainActivity.manggiohang.get(i).getSoluongsp() + sl);
                            if (MainActivity.manggiohang.get(i).getSoluongsp() >= 10){
                                MainActivity.manggiohang.get(i).setSoluongsp(10);
                                Toast.makeText(this, "Số lượng món " + f.getTensanpham() + " đã đạt max 10", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(this, "Thêm thành công vào giỏ hàng", Toast.LENGTH_SHORT).show();
                            }
                            MainActivity.manggiohang.get(i).setGiasp(giaa * MainActivity.manggiohang.get(i).getSoluongsp());
                            exists = true;
                        }
                    }
                    if (exists == false){
                        int soluong = 1;
                        long Giamoi = soluong * giaa;
                        MainActivity.manggiohang.add(new Giohang(iddd,t,Giamoi,h,soluong));
                    }
                }else {
                    int soluong = 1;
                    long Giamoi = soluong * giaa;
                    MainActivity.manggiohang.add(new Giohang(iddd,t,Giamoi,h,soluong));
                }
                break;
            case R.id.menu_themvaodsthich:
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Duongdanthemuathich, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")){
                            CheckConnection.ShowToast_Short(DoUongActivity.this,"Thêm thành công");

                        }else {
                            CheckConnection.ShowToast_Short(DoUongActivity.this,"lỗi");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CheckConnection.ShowToast_Short(DoUongActivity.this,"Lỗi server");
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("manguoidung",String.valueOf(LoginActivity.idnguoisudung));
                        params.put("masanpham",String.valueOf(iddd));
                        params.put("tensanpham",String.valueOf(t));
                        params.put("giasanpham",String.valueOf(giaa));
                        params.put("hinhanhsanpham",String.valueOf(h));
                        params.put("motasanpham",String.valueOf(motasp));
                        params.put("idsanpham",String.valueOf(loaisp));

                        return params;
                    }
                };
                requestQueue.add(stringRequest);

                break;
            case R.id.menu_themvaodskhongthich:
                RequestQueue requestQueue1 = Volley.newRequestQueue(this);
                StringRequest stringRequest1 = new StringRequest(Request.Method.POST, Server.Duongdanthemkhongthich, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")){
                            CheckConnection.ShowToast_Short(DoUongActivity.this,"Thêm thành công");

                        }else {
                            CheckConnection.ShowToast_Short(DoUongActivity.this,"lỗi");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CheckConnection.ShowToast_Short(DoUongActivity.this,"Lỗi server");
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("manguoidung",String.valueOf(LoginActivity.idnguoisudung));
                        params.put("masanpham",String.valueOf(iddd));
                        params.put("tensanpham",String.valueOf(t));
                        params.put("giasanpham",String.valueOf(giaa));
                        params.put("hinhanhsanpham",String.valueOf(h));
                        params.put("motasanpham",String.valueOf(motasp));
                        params.put("idsanpham",String.valueOf(loaisp));

                        return params;
                    }
                };
                requestQueue1.add(stringRequest1);

                break;

        }
        return super.onContextItemSelected(item);
    }
}
