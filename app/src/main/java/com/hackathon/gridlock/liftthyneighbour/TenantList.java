package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hackathon.gridlock.liftthyneighbour.vos.Tenant;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


//TODO Optional: make list searchable
public class TenantList extends Activity {


    private ArrayList<Tenant> tenants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_list);

        //populate List with Tenant info
        populateTenantList();
    }

    private void populateTenantList() {
        String token = getAdminToken();
        //TODO decide if userId is also required
        //TODO API call to getTenantList

        //dummy data
        Tenant one = new Tenant(1, "anil","a1","ka",123,"sdf@gmail.com");
        Tenant two = new Tenant(2, "banu","b2","tn",456,"abc@gmail.com");
        tenants = new ArrayList<Tenant>();
        tenants.add(one);
        tenants.add(two);

        populateList(tenants);
    }


    // having a separate function for population so that it can be called from API onResponse() inner method
    private void populateList(ArrayList<Tenant> tenants) {
        ListView tenantList = (ListView) findViewById(R.id.lvTenantList);
        ArrayList<String> tenantListItem = new ArrayList<String>();

        for (Tenant tenant: tenants) {
            String toDisplay = tenant.getUserName()+" (" + tenant.getFlatNumber() + " )";
            tenantListItem.add(toDisplay);
        }

        // Sort the tenants alphabetically
        Collections.sort(tenantListItem, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });


        // Populate the listView
        ArrayAdapter<String> tenantListAdapter = new ArrayAdapter<String>(
                                                            this,
                                                            android.R.layout.simple_list_item_1,
                                                            android.R.id.text1,
                                                            tenantListItem
                                                            );
        tenantList.setAdapter(tenantListAdapter);


        //set listener
        tenantList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showTenantDetails(position);
            }
        });
    }

    private void showTenantDetails(int position) {
        Tenant tenantSelected = tenants.get(position);
        Intent i = new Intent(this, TenantDetails.class);
        i.putExtra(TenantDetails.KEY_USER_ID,tenantSelected.getUserId());
        i.putExtra(TenantDetails.KEY_USER_NAME,tenantSelected.getUserName());
        i.putExtra(TenantDetails.KEY_USER_FLAT_NUMBER,tenantSelected.getFlatNumber());
        i.putExtra(TenantDetails.KEY_VEHICLE_NUMBER, tenantSelected.getVehicleNumber());
        i.putExtra(TenantDetails.KEY_CONTACT_NUMBER, tenantSelected.getContactNumber());
        i.putExtra(TenantDetails.KEY_EMAIL, tenantSelected.getEmail());
        startActivity(i);
    }

    private String getAdminToken() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.PREFERENCE_FILE_NAME), Context.MODE_PRIVATE);
        String tokenKey = getResources().getString(R.string.KEY_TOKEN);
        return sharedPreferences.getString(tokenKey, "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_home,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.aAHlogout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        purgeCredentials();
        redirectToSignInPage();
    }

    private void purgeCredentials() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.PREFERENCE_FILE_NAME), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.KEY_TOKEN), "");
        editor.putString(getString(R.string.KEY_USER_TYPE),"");
        editor.putBoolean(getString(R.string.KEY_IS_USER_ADMIN),false);
        editor.putBoolean(getString(R.string.KEY_IS_USER_SIGNED),false);
        editor.commit();
    }

    private void redirectToSignInPage() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
