package com.jonathanmackenzie.fileunifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Build;


public class MainActivity extends Activity// implements	DirectoryChooserFragment.OnFragmentInteractionListener  
{
	private Button btn;
	private static final String TAG = "MainActivity";
	private ProgressBar pb;
	private TextView textLog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		textLog = (TextView) findViewById(R.id.textLog);
		
		final ArrayAdapter<String> folderAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, new String[] {});
		
		ListView lv = (ListView) findViewById(R.id.listView1);
		btn = (Button) findViewById(R.id.buttonMoveFiles);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				btn.setText(getResources().getString(R.string.move_files));
				String[] folders = new String[folderAdapter.getCount()];
				for(int i = 0; i < folderAdapter.getCount(); i++) {
					folders[i] = folderAdapter.getItem(i);
				}
				MoverTask task = new MoverTask();
				task.execute(folders);
			}
		});
	}

	private class MoverTask extends AsyncTask<String, String, Long> {
		private int numFiles;
		private int processed;
		@Override
		protected void onPreExecute() {
			btn.setEnabled(false);
			pb.setVisibility(View.VISIBLE);
			processed = 0;
		}
		@Override
		protected Long doInBackground(String... foldersArray) {
			ArrayList<File> files = new ArrayList<File>(); 
			for (String folder : foldersArray) {
				files.addAll(FileUtils.listFiles(
						new File(folder), 
						TrueFileFilter.INSTANCE, 
						TrueFileFilter.INSTANCE));
			}
			pb.setMax(numFiles);
			numFiles = files.size();
			for (File file : files) {
				// Move the files over
				++processed;
				publishProgress(processed+": "+file.getAbsolutePath()+" -> ");
			}
			return 0l;
		}

		protected void onProgressUpdate(String... progress) {
			pb.setProgress(processed);
			btn.setText(String.format("%s %d/%d %s",
					getResources().getString(R.string.moved),
					progress[0],
					numFiles,
					getResources().getString(R.string.files)));
			textLog.append(progress[0]);
		}

		protected void onPostExecute(Long result) {
			btn.setEnabled(true);
			pb.setVisibility(View.GONE);
		}

	}

	private static void moveFile(String inputPath, String inputFile, String outputPath) {

		InputStream in = null;
		OutputStream out = null;
		try {

			// create output directory if it doesn't exist
			File dir = new File(outputPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			in = new FileInputStream(inputPath + inputFile);
			out = new FileOutputStream(outputPath + inputFile);

			IOUtils.copy(in, out);
			in.close();
			out.close();
			// delete the original file
			new File(inputPath + inputFile).delete();

		}

		catch (FileNotFoundException fnfe1) {
			Log.e(TAG, fnfe1.getMessage());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		} 

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



}
