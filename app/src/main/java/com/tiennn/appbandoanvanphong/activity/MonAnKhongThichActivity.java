package com.tiennn.appbandoanvanphong.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tiennn.appbandoanvanphong.R;
import com.tiennn.appbandoanvanphong.adapter.MonAnKhongThichAdapter;
import com.tiennn.appbandoanvanphong.adapter.MonAnUaThichAdapter;
import com.tiennn.appbandoanvanphong.model.Sanpham;
import com.tiennn.appbandoanvanphong.ultit.CheckConnection;
import com.tiennn.appbandoanvanphong.ultit.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MonAnKhongThichActivity extends AppCompatActivity {

    Toolbar toolbarkhongthich;
    ListView lvukhongthich;
    MonAnKhongThichAdapter monAnKhongThichAdapter;
    ArrayList<Sanpham> mangkhongthich;


    int id = 0;
    String Tendoan ="";
    int Giadoan = 0;
    String Hinhanhdoan = "";
    String Motadoan = "";
    int Idspdoan = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_an_khong_thich);

        Anhxa();
        ActionToolbar();
        GetData();
        LoadMoreData();

        registerForContextMenu(lvukhongthich); // khai báo context menu
    }



    private void LoadMoreData() {
        lvukhongthich.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),ChiTietSanPhamActivity.class);
                intent.putExtra("thongtinsanpham",mangkhongthich.get(i));
                startActivity(intent);
            }
        });

    }

    private void GetData() {
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Duongdankhongthich, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (response != null){
                        for (int i = 0 ; i < response.length() ; i++){

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            id = jsonObject.getInt("masanpham");
                            Tendoan = jsonObject.getString("tensanpham");
                            Giadoan = jsonObject.getInt("giasanpham");
                            Hinhanhdoan = jsonObject.getString("hinhanhsanpham");
                            Motadoan = jsonObject.getString("motasanpham");
                            Idspdoan = jsonObject.getInt("idsanpham");
                            mangkhongthich.add(new Sanpham(id , Tendoan , Giadoan , Hinhanhdoan , Motadoan , Idspdoan));

                            monAnKhongThichAdapter.notifyDataSetChanged();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CheckConnection.ShowToast_Short(getApplicationContext(),error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<String,String>();
                param.put("manguoidungapp",String.valueOf(LoginActivity.idnguoisudung));
                return param;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbarkhongthich);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarkhongthich.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void Anhxa() {
        toolbarkhongthich = findViewById(R.id.toolbarkhongthich);
        lvukhongthich = findViewById(R.id.listviewkhongthich);
        mangkhongthich = new ArrayList<>();
        monAnKhongThichAdapter = new MonAnKhongThichAdapter(getApplicationContext(),mangkhongthich);
        lvukhongthich.setAdapter(monAnKhongThichAdapter);


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
        getMenuInflater().inflate(R.menu.menu_context_delete, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Sanpham f = mangkhongthich.get(info.position);

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
        Sanpham f = mangkhongthich.get(info.position);



        switch (idMenu){
            case R.id.menu_xoa:
                break;

        }
        return super.onContextItemSelected(item);
    }

}
