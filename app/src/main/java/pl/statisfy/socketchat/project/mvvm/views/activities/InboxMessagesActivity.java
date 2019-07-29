package pl.statisfy.socketchat.project.mvvm.views.activities;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import pl.statisfy.socketchat.project.mvvm.views.fragments.InboxMessagesFragment;
import pl.statisfy.socketchat.project.interfaces.ActionBarTitleHandler;
import pl.statisfy.socketchat.project.model.User;
import pl.statisfy.socketchat.project.utils.Utils;

public class InboxMessagesActivity extends FullScreenActivity implements ActionBarTitleHandler {

    private static final String TAG = InboxMessagesActivity.class.getSimpleName();

    public ArrayList<String> friendDetails;

    @Override
    public Fragment createFragment() {
        friendDetails = getIntent().getStringArrayListExtra(Utils.EXTRA_FRIEND_DETAILS);
        return InboxMessagesFragment.newInstance(friendDetails);
    }

    public static Intent newInstance(Context context, User user){
        ArrayList<String> friendDetails = new ArrayList<>();
        friendDetails.add(user.getEmail());
        friendDetails.add(user.getUserPicture());
        friendDetails.add(user.getUserName());

        Intent intent = new Intent(context, InboxMessagesActivity.class);
        intent.putStringArrayListExtra(Utils.EXTRA_FRIEND_DETAILS, friendDetails);
        return intent;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof InboxMessagesFragment){
            ((InboxMessagesFragment) fragment).setActionBarTitleHandler(this);
        }
    }

    @Override
    public void changeTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}




