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

import com.hackathon.gridlock.liftthyneighbour.vos.Tenant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PendingApprovals extends Activity {

    private ArrayList<Tenant> pendingApprovals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_approvals);


        populatePendingApprovalsList();
    }

    private void populatePendingApprovalsList() {
        String token = getAdminToken();
        //TODO decide if userId is also required
        //TODO API call to getTenantList
        // TODO decide if it's fine for response to be ArrayList<Tenant>

        //dummy data
        Tenant one = new Tenant(1, "newAnil","a11","kaa",1233,"sdff@gmail.com");
        Tenant two = new Tenant(2, "newBanu","b22","tnn",4566,"abcc@gmail.com");
        pendingApprovals = new ArrayList<Tenant>();
        pendingApprovals.add(one);
        pendingApprovals.add(two);

        populateList(pendingApprovals);
    }


    // having a separate function for population so that it can be called from API onResponse() inner method
    private void populateList(ArrayList<Tenant> tenants) {
        ListView pendingList = (ListView) findViewById(R.id.lvPendingList);
        ArrayList<String> pendingListItem = new ArrayList<String>();

        for (Tenant tenant: tenants) {
            String toDisplay = tenant.getUserName()+" (" + tenant.getFlatNumber() + " )";
            pendingListItem.add(toDisplay);
        }

        // Sort the tenants alphabetically
        Collections.sort(pendingListItem, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });


        // Populate the listView
        ArrayAdapter<String> pendingListAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                pendingListItem
        );
        pendingList.setAdapter(pendingListAdapter);


        //set listener
        pendingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showPendingApproval(position);
            }
        });
    }

    private void showPendingApproval(int position) {
        Tenant pendingApprovalSelected = pendingApprovals.get(position);
        Intent i = new Intent(this, PendingApprovalDetails.class);
        i.putExtra(PendingApprovalDetails.KEY_USER_ID,pendingApprovalSelected.getUserId());
        i.putExtra(PendingApprovalDetails.KEY_USER_NAME,pendingApprovalSelected.getUserName());
        i.putExtra(PendingApprovalDetails.KEY_USER_FLAT_NUMBER,pendingApprovalSelected.getFlatNumber());
        i.putExtra(PendingApprovalDetails.KEY_VEHICLE_NUMBER, pendingApprovalSelected.getVehicleNumber());
        i.putExtra(PendingApprovalDetails.KEY_CONTACT_NUMBER, pendingApprovalSelected.getContactNumber());
        i.putExtra(PendingApprovalDetails.KEY_EMAIL, pendingApprovalSelected.getEmail());
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
