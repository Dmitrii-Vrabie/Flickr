package hfad.com.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by Mephisto on 4/7/2017.
 */

public class PhotoPageActivity extends SingleFragmentActivity {
    public static Intent newIntent(Context context, Uri photoPageUri){
        Intent intent = new Intent(context, PhotoPageActivity.class);
        intent.setData(photoPageUri);
        return intent;
    }
    @Override
    protected Fragment createFragment() {
        return PhotoPageFragment.newInstance(getIntent().getData());
    }
}
