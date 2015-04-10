package augier.fr.phoebius.UI

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import augier.fr.phoebius.core.MusicService


public class FragmentAdapter extends FragmentPagerAdapter {

    static final int NUM_ITEMS = 3;


    public FragmentAdapter(FragmentManager fm , MusicService ms) {
        super(fm, ms);
    }

    @Override
    public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub
        switch (arg0) {
            case 0:
                def frag = new SongListFragment(musicService)fragmentManager.beginTransaction().add(R.id.mainPager, frag).commit()
            case 1:
                def frag = new  AlbumListFragment(musicService)fragmentManager.beginTransaction().add(R.id.mainPager, frag).commit()
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
