package com.example.android.pingme;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/** 
 * this class is seleting package name so return sending server activity
 */
public class AppChoiceView extends ListActivity implements
		OnTouchListener, ListView.OnScrollListener {


	// TextView
	private TextView RunProcess = null;

	// 실행 중인 App이다.
	List<ActivityManager.RunningAppProcessInfo> appList2;

	// 설치된 어플리케이션의 리스트 이다.
	private ArrayList<Integer> items = new ArrayList();
	private PackageManager mPackageManager;
	ActivityManager activityManager;

	//선택한 정보를 저장한다.
	private int SeletedUid;
	private String SeletedPackageName;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Use a custom layout file
		setContentView(R.layout.traffic_list);

		// UI Thread에서 관리하는 View들이다.
		RunProcess = (TextView) findViewById(R.id.Traffic_RunProcessText);
		

		// Use our own list adapter
		// ListActivity를 상속 받았기 떄문에 이런식으로 처리 한다.
		// Self는 자기 자신에 대한 객체 이다.
		this.getListView().setOnTouchListener(this); // 리스너를 등록해 준다.

		
		// 관리자를
		// 얻어온다.

		mPackageManager = getPackageManager();

		// 실행중인 프로세스의 리스트를 얻어오기 위함 이다.
		activityManager = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		appList2 = activityManager.getRunningAppProcesses();

		Iterator it = appList2.iterator();
		// 순회 하면서 실행중 프로세서들의 pid값을 얻어온다.
		while (it.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) it
					.next();
			items.add(info.pid);
		}
		
		setListAdapter(new ProcessListAdapter(this,appList2)); //커스터마이즈한 어뎁터를 달아준다.
		/*setListAdapter(new MyAdapter(this, R.layout.my_app_row,
		R.id.traffic_pid, appList2));*/
	}

	// 어뎁터를 갱신해주는 기능을 한다.
	public void onRefresh() {

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	// create에서 ProcessInfoQuery 클래스의 Thread를 수행 한다면 이건에서는 JIN로 작성된 Native
	// Thread를 수행한다.
	@Override
	protected void onResume() {
		super.onResume();
	}

	// ArrayAdapter를 상속한 커스텀 어댑터 --> 데이터를 보기 좋게 커스터마이징
	class MyAdapter extends ArrayAdapter {

		public MyAdapter(Context context, int resource, int textViewResourceId,
				List items) {
			super(context, resource, textViewResourceId, items);
			// TODO Auto-generated constructor stub
		}

		//커서가 아닌것은 결국 getView만 구현 하면 된다.
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			View row = getLayoutInflater().inflate(R.layout.my_app_row, null);

			// 현재 위치의 아이템을 얻어온다.
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) getItem(position);

			TextView PidTextView = (TextView) row
					.findViewById(R.id.traffic_pid);
			TextView ApplciationNameTextView = (TextView) row
					.findViewById(R.id.traffic_application_name);
			ImageView IconTextView = (ImageView) row
					.findViewById(R.id.traffic_appicon);

			PidTextView.setText(String.format("%d", info.pid));
			ApplciationNameTextView.setText(info.processName);
			try {
				IconTextView.setImageDrawable(mPackageManager
						.getApplicationIcon(mPackageManager
								.getNameForUid(info.uid)));
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return row;
		}
	}

	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		//Seleting uid and package is saving to golobal various.
		SeletedUid = appList2.get(position).uid;
		SeletedPackageName = mPackageManager.getNameForUid(appList2.get(position).uid);
		showMessageDialog("You want this information?", true);
		
	}
	/**
	 * 처리 결과를 출력해주는 Diaglog 이다. 
	 */
	public void showMessageDialog(final CharSequence message, final boolean success) {
		runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog dialog = null;
				AlertDialog.Builder builder = new AlertDialog.Builder(AppChoiceView.this);
				builder.setMessage(message+" "+SeletedUid+" "+SeletedPackageName);
				builder.setNegativeButton("ok", null);
				builder.setIcon(success ? android.R.drawable.ic_dialog_info :
					android.R.drawable.ic_dialog_alert);
				builder.setTitle(success ? "success" : "error");
				builder.setPositiveButton("ok",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.putExtra("uid", SeletedUid);
						intent.putExtra("package_name", SeletedPackageName);
						setResult(RESULT_OK, intent);
						finish();
					}
				});
				builder.setNegativeButton("cancel",new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
							//취소 했다는 메시지를 출력한다.
						Toast.makeText(AppChoiceView.this, 
								"cancel", Toast.LENGTH_SHORT).show();
					}
				});
				dialog = builder.create();
				dialog.show();
			}
		});
	}
	// 커스터 마이즈한 리스트 어뎁터 이다. 결국 모든것은 이 어뎁터에 의해서 수행되어 질 것이다.
	private class ProcessListAdapter extends BaseAdapter {

		public ProcessListAdapter(Context context, List<ActivityManager.RunningAppProcessInfo> arList) {
			mContext = context;
			mList = arList;
		}

		public int getCount() {
			
			return appList2.size();
		}

		public Object getItem(int position) {
			return position; // 현재 위치에대한 정보
		}

		public long getItemId(int position) {
			return position; // 현재 아이디에 대한 정보
		}

		// 실제적으로 ListView를 채우는 코드이다.
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ProcessDetailView sv = null;
			
			PackageInfo appPackageInfo = null;
			// 현재 위치의 아이템을 얻어온다.
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) mList.get(position);
						
			//이름에 ':'문자를 포함하고 있다면 :전까지의 내용을 PackageName으로 정의한다. 이렇게 해야 정상적으로 icon과 label을 추출 할 수 있다.	
			if(info.processName.contains(":")){
				PackageName = info.processName.substring(0,info.processName.indexOf(":"));
			}
			else
				PackageName = info.processName;
			
			//해당 PackageName에 대한 정보를 PackageManager로 부터 받아 온다. 
			try {
				appPackageInfo = mPackageManager.getPackageInfo(PackageName, 0);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//PackageName에 대한 정보가 없을경우 SearchObj에서 UID를 얻어온다.
			if(appPackageInfo == null && info.uid >0)
			{
				//UID를 통해서 Package Name을 확보 한다.
				String[] subPackageName = mPackageManager.getPackagesForUid(info.uid);
					
				if(subPackageName != null)
				{
					for(int PackagePtr = 0; PackagePtr < subPackageName.length; PackagePtr++)
					{
						if (subPackageName[PackagePtr] == null)
							continue;
						
						try {  
							appPackageInfo = mPackageManager.getPackageInfo(subPackageName[PackagePtr], 0);
							PackagePtr = subPackageName.length;
						} catch (NameNotFoundException e) {}						
					}
				}
			}
			//페키지 이름으로는 식별이 힘들기 때문에 페키지이름을 어플리케이션 라벨 이름으로 변환 시켜준다.
			LabelName = appPackageInfo.applicationInfo.loadLabel(mPackageManager).toString();
			Icon = resizeImage(appPackageInfo.applicationInfo.loadIcon(mPackageManager));
			
		/*	if (convertView == null) {
			*/	
					sv = new ProcessDetailView(mContext,Icon, info.pid, info.uid, LabelName,position);
			
					/*	} else {
				sv = (ProcessDetailView) convertView;
					sv.setView(Icon,
									info.pid,info.uid, 
									LabelName, position);
			
			
			}*/

			return sv;
		}

		private Context mContext;
		private List<ActivityManager.RunningAppProcessInfo> mList;
		private String PackageName = null;
		private String LabelName = null;
		public Drawable Icon;
	}
	
	/*
	 * ListView의 한셀 한셀을 나타낸다. Expanded의 값에 따라서 자세한 정보가 보여질지 안보여질지를 결정 한다.
	 * TableLayout을 상속 받아서 프로세스 정보를 표시해 주는 용도로 완성 시킨다.
	 */
	private class ProcessDetailView extends TableLayout {

		private TableRow TitleRow;
		private TextView PIDField;
		private ImageView IconField;
		private TextView NameField;

		public ProcessDetailView(Context context,Drawable Icon, int PID ,int UID,
				String Name,int position) {
			super(context);
			this.setColumnStretchable(2, true);

			// this.setOrientation(VERTICAL);

			PIDField = new TextView(context);// 프로세스 ID를 표시한다.
			IconField = new ImageView(context);// 어플리케이션 아이콘을 표시 한다.
			NameField = new TextView(context);// 어플리케이션의 이름을 표시 한다.
			
			PIDField.setText("" + PID+"("+UID+")"); // PID 정보를 삽입한다.

			IconField.setImageDrawable(Icon);
			IconField.setPadding(8, 3, 3, 3);

			NameField.setText(Name);

			PIDField.setGravity(Gravity.LEFT);
			PIDField.setPadding(5, 3, 6, 3);

			NameField.setPadding(3, 3, 3, 3);
			NameField.setGravity(Gravity.LEFT);
			NameField.setWidth(getWidth() - IconField.getWidth());
	

			/*
			 * 최종적으로 TableRow를 셍상히야 값을 달아준다. PID Icon(application) Name
			 * Value(load값) DetailField(Icon)
			 */
			TitleRow = new TableRow(context);
			TitleRow.addView(PIDField);
			TitleRow.addView(IconField);
			TitleRow.addView(NameField);
			addView(TitleRow);
					
			// 포지션마다 백그라운드 색을 다르게 한다.
			if (position % 2 == 0)
				setBackgroundColor(0x80444444);
			else
				setBackgroundColor(0x80000000);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	//applciation icon을 resize해줘야 같은 크기로 List에 출력 되어 지게된다.
	private Drawable resizeImage(Drawable Icon) {

		if(CompareFunc.getScreenSize() == 2)
		{
			Bitmap BitmapOrg = Bitmap.createBitmap(60, 60, Bitmap.Config.ARGB_8888); 
			Canvas BitmapCanvas = new Canvas(BitmapOrg);
			Icon.setBounds(0, 0, 60, 60);
			Icon.draw(BitmapCanvas); 
	        return new BitmapDrawable(BitmapOrg);
		}
		else if (CompareFunc.getScreenSize() == 0)
		{
			Bitmap BitmapOrg = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888); 
			Canvas BitmapCanvas = new Canvas(BitmapOrg);
			Icon.setBounds(0, 0, 10, 10);
			Icon.draw(BitmapCanvas); 
	        return new BitmapDrawable(BitmapOrg);
		}
		else
		{
			Bitmap BitmapOrg = Bitmap.createBitmap(22, 22, Bitmap.Config.ARGB_8888); 
			Canvas BitmapCanvas = new Canvas(BitmapOrg);
			Icon.setBounds(0, 0, 22, 22);
			Icon.draw(BitmapCanvas); 
	        return new BitmapDrawable(BitmapOrg);
		}
    }
}