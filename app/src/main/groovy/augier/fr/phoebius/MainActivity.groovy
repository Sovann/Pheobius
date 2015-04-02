package augier.fr.phoebius

import android.app.ActionBar
import android.app.ActionBar.Tab
import android.app.ActionBar.TabListener
import android.app.Activity
import android.app.FragmentTransaction
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.MediaController
import android.widget.MediaController.MediaPlayerControl
import augier.fr.phoebius.UI.FragmentAdapter
import augier.fr.phoebius.UI.SongAdapter
import augier.fr.phoebius.core.MusicService
import augier.fr.phoebius.core.MusicService.MusicBinder
import augier.fr.phoebius.utils.SongList
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import com.arasthel.swissknife.annotations.OnItemClick

public class MainActivity extends FragmentActivity implements MediaPlayerControl, TabListener
{
	public static final String APP_NAME = "Phoebius"
	@InjectView ListView songView
	private SongList songList
	private MusicService musicService
	private Intent playIntent
	private boolean musicBound = false
	private MusicServiceConnection musicConnection
	private MusicController musicController
	private MediaPlayerControl mediaPlayerControl = this
    ActionBar actionbar;
    ViewPager viewpager;
    FragmentAdapter ft;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Class init
		super.onCreate(savedInstanceState)
		contentView = R.layout.activity_main
		SwissKnife.inject(this)

		// Variables init
		songList = new SongList(contentResolver)
		musicConnection = new MusicServiceConnection()

		// UI init
		SongAdapter songAdapter = new SongAdapter(this, songList.songList)
		songView.setAdapter(songAdapter)
		musicController = new MusicController(this)
		musicController.setPrevNextListeners(
			new View.OnClickListener() { @Override public void onClick(View v) { playNext() }},
			new View.OnClickListener() { @Override public void onClick(View v) { playPrev() }})

        viewpager = (ViewPager) findViewById(R.id.pager);
        ft = new FragmentAdapter(getSupportFragmentManager());

        actionbar = getActionBar();
        viewpager.setAdapter(ft);
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionbar.addTab(actionbar.newTab().setText("Playlist").setTabListener(this));
        actionbar.addTab(actionbar.newTab().setText("Album").setTabListener(this));
        actionbar.addTab(actionbar.newTab().setText("Artist").setTabListener(this));
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                actionbar.setSelectedNavigationItem(arg0);

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewpager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub

    }
	@Override
	protected void onStart()
	{
		super.onStart()

		if(playIntent == null)
		{
			playIntent = new Intent(this, MusicService.class)
			bindService(playIntent, musicConnection, BIND_AUTO_CREATE)
			startService(playIntent)
		}
	}

	@Override
	protected void onDestroy()
	{
		stopService(playIntent)
		musicService = null
		super.onDestroy()
	}

	private void playNext(){
		musicService.playNext()
		musicController.show()
	}

	private void playPrev(){
		musicService.playPrevious()
		musicController.show()
	}

	// region User events callbacks
	/* TODO: Changer la méthode de lecture
	 * Le cas où la musique n'existe plus (elle a été effacée ou autre)
	 * n'est pas géré. Il faut pouvoir associer à la musique un ID
	 * unique qui assure que la musique jouée est bien celle qui a
	 * été cliquée. Si la musique cliquée n'est plus dans la liste,
	 * alors un message d'erreur doit apparaitre.
	 */
	@OnItemClick(R.id.songView)
	public void onItemClick(int position)
	{
		musicService.play(position)
		musicController.show()
	}

	@Override
	void start(){ musicService.start() }

	@Override
	void pause(){ musicService.pause() }

	@Override
	int getDuration()
	{
		if(musicService == null){ return 0 }
		return musicService.playing ? musicService.duration : 0
	}

	@Override
	int getCurrentPosition()
	{
		if(musicService == null){ return 0 }
		return musicService.playing ? musicService.songPosition : 0
	}

	@Override
	void seekTo(int i){}

	@Override
	boolean isPlaying(){ return musicService != null ? musicService.playing : false }

	@Override
	int getBufferPercentage(){ return 0 }

	@Override
	boolean canPause(){ return true }

	@Override
	boolean canSeekBackward(){ return true }

	@Override
	boolean canSeekForward(){ return true }

	@Override
	int getAudioSessionId(){ return 0 }
	//endregion

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menuInflater.inflate(R.menu.main, menu)
		return true
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.itemId)
		{
			case R.id.action_shuffle:
				break
			case R.id.action_end:
				stopService(playIntent)
				musicService = null
				System.exit(0)
				break
		}
		return super.onOptionsItemSelected(item)
	}

	private class MusicServiceConnection implements ServiceConnection
	{
		@Override
		void onServiceConnected(ComponentName componentName, IBinder iBinder)
		{
			MusicBinder binder = iBinder as MusicBinder
			musicService = binder.service
			/* TODO: Retirer l'injection de songList
			 * La liste des fichiers devrait appartenir au service lui-même
			 * Cela permettra plus tard de ne pas avoir à se soucier
			 * de sa sauvegarde lors des changements d'activité
			 * (flip de l'écran, passe sur un widget)...
			 */
			musicService.songList = songList
			musicBound = true
		}

		@Override
		void onServiceDisconnected(ComponentName componentName){ musicBound = false }
	}

	private class MusicController extends MediaController
	{
		MusicController(Context context)
		{
			super(context)

			mediaPlayer = mediaPlayerControl
			anchorView = songView
			enabled = true
		}

		@Override
		public void hide(){}
	}
}