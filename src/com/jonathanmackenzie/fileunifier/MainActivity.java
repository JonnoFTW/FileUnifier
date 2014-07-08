package com.jonathanmackenzie.fileunifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
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
import android.os.Build;

public class MainActivity extends Activity {
	private Button btn;
	private ProgressBar pb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		pb.setMax(100);
		final ArrayAdapter<String> folderAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, new String[] {});
		ListView lv = (ListView) findViewById(R.id.listView1);
		btn = (Button) findViewById(R.id.buttonMoveFiles);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				btn.setEnabled(false);
				final ArrayList<String> folders = new ArrayList<String>();
				for(int i = 0; i < folderAdapter.getCount(); i++) {
					folders.add(folderAdapter.getItem(i));
				}
				MoverTask task = new MoverTask();
				task.execute(folders);
			}
		});
	}

	private class MoverTask extends AsyncTask<ArrayList<String>, Integer, Long> {
		@Override
		protected Long doInBackground(ArrayList<String>... foldersArray) {
			
			ArrayList<String> folders = foldersArray[0];
			ArrayList<File> files = new ArrayList<File>(); 
			for (String folder : folders) {
				files.addAll(FileUtils.listFiles(new File(folder), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE));
			}
			
			return 0l;
		}

		protected void onProgressUpdate(Integer... progress) {
			pb.setProgress(progress[0]);
		}

		protected void onPostExecute(Long result) {
			btn.setEnabled(true);
		}

	}

	private void moveFile(String inputPath, String inputFile, String outputPath) {

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

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;

			// write the output file
			out.flush();
			out.close();
			out = null;

			// delete the original file
			new File(inputPath + inputFile).delete();

		}

		catch (FileNotFoundException fnfe1) {
			Log.e("tag", fnfe1.getMessage());
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_DIRECTORY) {
			if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
				handleDirectoryChoice(data
						.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
			} else {
				// Nothing selected
			}
		}
	}

}
