
package com.baasio.sample.startup;

import static com.kth.common.utils.LogUtils.LOGE;
import static com.kth.common.utils.LogUtils.makeLogTag;

import com.kth.baasio.Baasio;

import org.codehaus.jackson.JsonNode;
import org.usergrid.android.client.callbacks.ApiResponseCallback;
import org.usergrid.java.client.entities.Entity;
import org.usergrid.java.client.response.ApiResponse;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends Activity {
    private static final String TAG = makeLogTag(MainActivity.class);

    private Context mContext;

    private Button mBaasio;

    private String mCreatedUuid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        Entity entity = new Entity("foo");
        entity.setProperty("greeting", "Hello!! baas.io!!");

        Baasio.getInstance().createEntityAsync(entity, new ApiResponseCallback() {

            @Override
            public void onException(Exception e) {
                LOGE(TAG, "createEntityAsync:" + e.getMessage());
            }

            @Override
            public void onResponse(ApiResponse response) {
                if (response != null) {
                    if (TextUtils.isEmpty(response.getError())) {
                        Entity entity = response.getFirstEntity();
                        Map<String, JsonNode> map = entity.getProperties();

                        JsonNode node = map.get("greeting");

                        mCreatedUuid = entity.getUuid().toString();
                        Toast.makeText(mContext, "Create Success: " + node.getTextValue(),
                                Toast.LENGTH_LONG).show();
                    } else {
                        LOGE(TAG, "createEntityAsync:" + response.getErrorDescription());
                    }
                }
            }
        });

        mBaasio = (Button)findViewById(R.id.btnBaasio);
        mBaasio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Baasio.getInstance().queryEntitiesRequestAsync(new ApiResponseCallback() {

                    @Override
                    public void onException(Exception e) {
                        LOGE(TAG, "queryEntitiesRequestAsync:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(ApiResponse response) {
                        if (response != null) {
                            if (TextUtils.isEmpty(response.getError())) {
                                Entity entity = response.getFirstEntity();
                                Map<String, JsonNode> map = entity.getProperties();

                                JsonNode node = map.get("greeting");

                                Toast.makeText(mContext, "Query Success: " + node.getTextValue(),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                if (!TextUtils.isEmpty(response.getErrorDescription())) {
                                    Toast.makeText(mContext, response.getErrorDescription(),
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(mContext, response.getError(), Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        }
                    }
                }, "greeting", mCreatedUuid);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
