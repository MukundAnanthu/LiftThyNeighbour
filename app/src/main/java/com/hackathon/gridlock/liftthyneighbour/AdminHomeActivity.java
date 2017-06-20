package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class AdminHomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        ListView adminOpsListView = (ListView) findViewById(R.id.lvAdminOps);
        adminOpsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //Pending Approvals
                        switchToPendingApprovalsActivity();
                        break;
                    case 1:
                        //Tenant List
                        switchToTenantListActivity();
                }
            }
        });
    }

    private void switchToPendingApprovalsActivity() {
        Intent i = new Intent(this, PendingApprovals.class);
        startActivity(i);
    }

    private void switchToTenantListActivity() {
        Intent i = new Intent(this, TenantList.class);
        startActivity(i);
    }
}
