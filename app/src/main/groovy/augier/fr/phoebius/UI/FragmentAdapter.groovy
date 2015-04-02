package augier.fr.phoebius.UI

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import augier.fr.phoebius.AlbumFragment
import augier.fr.phoebius.ArtistFragment
import augier.fr.phoebius.PlaylistFragment

/**
 * Created by So on 29/03/2015.
 */
public class FragmentAdapter extends FragmentPagerAdapter {

    static final int NUM_ITEMS = 3;



    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub
        switch (arg0) {
            case 0:
                return new ArtistFragment();
            case 1:
                return new AlbumFragment();
            case 2:
                return new PlaylistFragment();
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return NUM_ITEMS;
    }
}
