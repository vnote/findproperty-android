package com.cetnaline.findproperty.ui.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.lbsapi.panoramaview.PanoramaViewListener;
import com.baidu.lbsapi.panoramaview.TextMarker;
import com.baidu.lbsapi.tools.CoordinateConverter;
import com.baidu.lbsapi.tools.Point;
import com.baidu.mapapi.model.LatLng;
import com.cetnaline.findproperty.BaseApplication;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.NearbyFragment;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;

/**
 * Created by fanxl2 on 2016/12/13.
 */
public class PanoramaActivity extends BaseActivity {

	public static final String PANORAMA_MARK_NAME = "PANORAMA_MARK_NAME";

	@BindView(R.id.panorama_pv)
	PanoramaView panorama_pv;

	@BindView(R.id.back_img)
	ImageView back_img;

	private boolean initDirect = true;

	@Override
	protected int getContentViewId() {
		return R.layout.act_panorama;
	}

	@Override
	protected void initBeforeSetContentView() {
		super.initBeforeSetContentView();

		if (mApp.mBMapManager == null) {
			mApp.mBMapManager = new BMapManager(mApp);
			mApp.mBMapManager.init(new BaseApplication.MyGeneralListener());
		}
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		//是否显示邻接街景箭头（有邻接全景的时候）
		panorama_pv.setShowTopoLink(true);
		back_img.setOnClickListener(v->onBackPressed());

		//设置全景图片的显示级别
		//根据枚举类ImageDefinition来设置清晰级别
		//较低清晰度 ImageDefinationLow
		//中等清晰度 ImageDefinationMiddle
		//较高清晰度 ImageDefinationHigh
		panorama_pv. setPanoramaImageLevel(PanoramaView.ImageDefinition.ImageDefinitionMiddle);

		double lng = getIntent().getDoubleExtra(NearbyFragment.LONGITUDE_KEY, 116.307894);
		double lat = getIntent().getDoubleExtra(NearbyFragment.LATITUDE_KEY, 39.984122);

		String name = getIntent().getStringExtra(PANORAMA_MARK_NAME);

		panorama_pv.setPanorama(lng, lat);

		TextMarker marker = new TextMarker();
		marker.setMarkerPosition(new Point(lng, lat));
		marker.setMarkerHeight(10f);
		marker.setFontColor(0xFFFFFFFF);
		marker.setText(name);
		marker.setFontSize(12);
		marker.setBgColor(0x80FFFFFF);
		marker.setPadding(20, 10, 20, 10);
		panorama_pv.addMarker(marker);
		panorama_pv.setPoiMarkerVisibility(true);
		//		panorama_pv.setPanoramaHeading(315.0f);

		panorama_pv.setPanoramaViewListener(new PanoramaViewListener() {
			@Override
			public void onDescriptionLoadEnd(String s) {

			}

			@Override
			public void onLoadPanoramaBegin() {

			}

			@Override
			public void onLoadPanoramaEnd(String s) {
//				Logger.i("buobao:"+ s);
//				Logger.i("buobao:"+ panorama_pv.getPanoramaHeading());
//				Logger.i("buobao:"+lat+","+lng);
//
//				Gson gson = new Gson();
//				PanoramBean bean = gson.fromJson(s, PanoramBean.class);
//				Point point = CoordinateConverter.MCConverter2LL(bean.X,bean.Y);
//				Logger.i("buobao:"+point.x+","+point.y);
//
//				double a = Math.sqrt((point.x - lng)*(point.x - lng) + (point.y - lat)*(point.y - lat));
//				double b = Math.sqrt()
				if (initDirect) {
					panorama_pv.setPanoramaHeading(panorama_pv.getPanoramaHeading() + 45);
					initDirect = false;
				}
			}

			@Override
			public void onLoadPanoramaError(String s) {

			}

			@Override
			public void onMessage(String s, int i) {

			}

			@Override
			public void onCustomMarkerClick(String s) {

			}
		});
	}

	@Override
	protected IPresenter createPresenter() {
		return null;
	}

	@Override
	protected void initToolbar() {
		showToolbar(false);
		StatusBarCompat.translucentStatusBar(this, true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		panorama_pv.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		panorama_pv.onResume();
	}

	@Override
	protected void onDestroy() {
		panorama_pv.destroy();
		super.onDestroy();
	}



	public static class PanoramBean{

		/**
		 * format_v : 0
		 * PoiDir : null
		 * Type : street
		 * TimeLine : [{"ID":"09000300011606241414101474C","Year":"2016","Time":"day","IsCurrent":1,"TimeDir":"","TimeLine":"201606"},{"ID":"09000300001501191353045046N","Year":"2015","Time":"day","IsCurrent":0,"TimeDir":"obsolete","TimeLine":"201501"}]
		 * Heading : 161.58
		 * UserID :
		 * MoveDir : 161.579
		 * Obsolete : 0
		 * ImgLayer : [{"BlockY":1,"BlockX":2,"ImgFormat":"jpg","ImgLevel":1},{"BlockY":2,"BlockX":4,"ImgFormat":"jpg","ImgLevel":2},{"BlockY":4,"BlockX":8,"ImgFormat":"jpg","ImgLevel":3},{"BlockY":8,"BlockX":16,"ImgFormat":"jpg","ImgLevel":4}]
		 * FileTag : pano_cfg
		 * Roads : [{"ID":"fd789a-a6a9-9382-9688-b98896","Name":"东泰路","Width":550,"IsCurrent":1,"Panos":[{"Order":0,"DIR":161,"Y":364137192,"PID":"09000300011606241414065844C","X":1352697895,"Type":"street"},{"Order":1,"DIR":161,"Y":364134859,"PID":"09000300011606241414082764C","X":1352698724,"Type":"street"},{"Order":2,"DIR":0,"Y":364132505,"PID":"09000300011606241414101474C","X":1352699521,"Type":"street"}]},{"ID":"aefdd8-46fc-5679-4e04-82c2b7","Name":"东泰路","IsCurrent":0,"Width":550}]
		 * Time : 201606
		 * Date : 20160624
		 * SwitchID : []
		 * ID : 09000300011606241414101474C
		 * Version : 0
		 * Y : 3641325
		 * Rname : 东泰路
		 * X : 13526995
		 * NorthDir : 108.42
		 * Z : 15.42
		 * procdate : 20170110
		 * LayerCount : 4
		 * Admission : GS(2015)2210
		 * Mode : day
		 * Roll : 0.237
		 * Links : [{"CPointY":364129890,"PID":"09000300011606241414129544C","CPointX":1352700397,"Type":"street","RID":"aefdd8-46fc-5679-4e04-82c2b7","DIR":161,"Y":364129174,"X":1352700631}]
		 * plane :
		 * RX : 13527001
		 * DeviceHeight : 2.32
		 * RY : 3641326
		 * panoinfo : null
		 * Provider : 9
		 * Pitch : 0.057
		 */

		private String format_v;
		private Object PoiDir;
		private String Type;
		private double Heading;
		private String UserID;
		private double MoveDir;
		private int Obsolete;
		private String FileTag;
		private String Time;
		private String Date;
		private String ID;
		private String Version;
		private int Y;
		private String Rname;
		private int X;
		private double NorthDir;
		private double Z;
		private String procdate;
		private int LayerCount;
		private String Admission;
		private String Mode;
		private double Roll;
		private String plane;
		private int RX;
		private double DeviceHeight;
		private int RY;
		private Object panoinfo;
		private int Provider;
		private double Pitch;
		private List<TimeLineBean> TimeLine;
		private List<ImgLayerBean> ImgLayer;
		private List<RoadsBean> Roads;
		private List<?> SwitchID;
		private List<LinksBean> Links;

		public String getFormat_v() {
			return format_v;
		}

		public void setFormat_v(String format_v) {
			this.format_v = format_v;
		}

		public Object getPoiDir() {
			return PoiDir;
		}

		public void setPoiDir(Object PoiDir) {
			this.PoiDir = PoiDir;
		}

		public String getType() {
			return Type;
		}

		public void setType(String Type) {
			this.Type = Type;
		}

		public double getHeading() {
			return Heading;
		}

		public void setHeading(double Heading) {
			this.Heading = Heading;
		}

		public String getUserID() {
			return UserID;
		}

		public void setUserID(String UserID) {
			this.UserID = UserID;
		}

		public double getMoveDir() {
			return MoveDir;
		}

		public void setMoveDir(double MoveDir) {
			this.MoveDir = MoveDir;
		}

		public int getObsolete() {
			return Obsolete;
		}

		public void setObsolete(int Obsolete) {
			this.Obsolete = Obsolete;
		}

		public String getFileTag() {
			return FileTag;
		}

		public void setFileTag(String FileTag) {
			this.FileTag = FileTag;
		}

		public String getTime() {
			return Time;
		}

		public void setTime(String Time) {
			this.Time = Time;
		}

		public String getDate() {
			return Date;
		}

		public void setDate(String Date) {
			this.Date = Date;
		}

		public String getID() {
			return ID;
		}

		public void setID(String ID) {
			this.ID = ID;
		}

		public String getVersion() {
			return Version;
		}

		public void setVersion(String Version) {
			this.Version = Version;
		}

		public int getY() {
			return Y;
		}

		public void setY(int Y) {
			this.Y = Y;
		}

		public String getRname() {
			return Rname;
		}

		public void setRname(String Rname) {
			this.Rname = Rname;
		}

		public int getX() {
			return X;
		}

		public void setX(int X) {
			this.X = X;
		}

		public double getNorthDir() {
			return NorthDir;
		}

		public void setNorthDir(double NorthDir) {
			this.NorthDir = NorthDir;
		}

		public double getZ() {
			return Z;
		}

		public void setZ(double Z) {
			this.Z = Z;
		}

		public String getProcdate() {
			return procdate;
		}

		public void setProcdate(String procdate) {
			this.procdate = procdate;
		}

		public int getLayerCount() {
			return LayerCount;
		}

		public void setLayerCount(int LayerCount) {
			this.LayerCount = LayerCount;
		}

		public String getAdmission() {
			return Admission;
		}

		public void setAdmission(String Admission) {
			this.Admission = Admission;
		}

		public String getMode() {
			return Mode;
		}

		public void setMode(String Mode) {
			this.Mode = Mode;
		}

		public double getRoll() {
			return Roll;
		}

		public void setRoll(double Roll) {
			this.Roll = Roll;
		}

		public String getPlane() {
			return plane;
		}

		public void setPlane(String plane) {
			this.plane = plane;
		}

		public int getRX() {
			return RX;
		}

		public void setRX(int RX) {
			this.RX = RX;
		}

		public double getDeviceHeight() {
			return DeviceHeight;
		}

		public void setDeviceHeight(double DeviceHeight) {
			this.DeviceHeight = DeviceHeight;
		}

		public int getRY() {
			return RY;
		}

		public void setRY(int RY) {
			this.RY = RY;
		}

		public Object getPanoinfo() {
			return panoinfo;
		}

		public void setPanoinfo(Object panoinfo) {
			this.panoinfo = panoinfo;
		}

		public int getProvider() {
			return Provider;
		}

		public void setProvider(int Provider) {
			this.Provider = Provider;
		}

		public double getPitch() {
			return Pitch;
		}

		public void setPitch(double Pitch) {
			this.Pitch = Pitch;
		}

		public List<TimeLineBean> getTimeLine() {
			return TimeLine;
		}

		public void setTimeLine(List<TimeLineBean> TimeLine) {
			this.TimeLine = TimeLine;
		}

		public List<ImgLayerBean> getImgLayer() {
			return ImgLayer;
		}

		public void setImgLayer(List<ImgLayerBean> ImgLayer) {
			this.ImgLayer = ImgLayer;
		}

		public List<RoadsBean> getRoads() {
			return Roads;
		}

		public void setRoads(List<RoadsBean> Roads) {
			this.Roads = Roads;
		}

		public List<?> getSwitchID() {
			return SwitchID;
		}

		public void setSwitchID(List<?> SwitchID) {
			this.SwitchID = SwitchID;
		}

		public List<LinksBean> getLinks() {
			return Links;
		}

		public void setLinks(List<LinksBean> Links) {
			this.Links = Links;
		}

		public static class TimeLineBean {
			/**
			 * ID : 09000300011606241414101474C
			 * Year : 2016
			 * Time : day
			 * IsCurrent : 1
			 * TimeDir :
			 * TimeLine : 201606
			 */

			private String ID;
			private String Year;
			private String Time;
			private int IsCurrent;
			private String TimeDir;
			private String TimeLine;

			public String getID() {
				return ID;
			}

			public void setID(String ID) {
				this.ID = ID;
			}

			public String getYear() {
				return Year;
			}

			public void setYear(String Year) {
				this.Year = Year;
			}

			public String getTime() {
				return Time;
			}

			public void setTime(String Time) {
				this.Time = Time;
			}

			public int getIsCurrent() {
				return IsCurrent;
			}

			public void setIsCurrent(int IsCurrent) {
				this.IsCurrent = IsCurrent;
			}

			public String getTimeDir() {
				return TimeDir;
			}

			public void setTimeDir(String TimeDir) {
				this.TimeDir = TimeDir;
			}

			public String getTimeLine() {
				return TimeLine;
			}

			public void setTimeLine(String TimeLine) {
				this.TimeLine = TimeLine;
			}
		}

		public static class ImgLayerBean {
			/**
			 * BlockY : 1
			 * BlockX : 2
			 * ImgFormat : jpg
			 * ImgLevel : 1
			 */

			private int BlockY;
			private int BlockX;
			private String ImgFormat;
			private int ImgLevel;

			public int getBlockY() {
				return BlockY;
			}

			public void setBlockY(int BlockY) {
				this.BlockY = BlockY;
			}

			public int getBlockX() {
				return BlockX;
			}

			public void setBlockX(int BlockX) {
				this.BlockX = BlockX;
			}

			public String getImgFormat() {
				return ImgFormat;
			}

			public void setImgFormat(String ImgFormat) {
				this.ImgFormat = ImgFormat;
			}

			public int getImgLevel() {
				return ImgLevel;
			}

			public void setImgLevel(int ImgLevel) {
				this.ImgLevel = ImgLevel;
			}
		}

		public static class RoadsBean {
			/**
			 * ID : fd789a-a6a9-9382-9688-b98896
			 * Name : 东泰路
			 * Width : 550
			 * IsCurrent : 1
			 * Panos : [{"Order":0,"DIR":161,"Y":364137192,"PID":"09000300011606241414065844C","X":1352697895,"Type":"street"},{"Order":1,"DIR":161,"Y":364134859,"PID":"09000300011606241414082764C","X":1352698724,"Type":"street"},{"Order":2,"DIR":0,"Y":364132505,"PID":"09000300011606241414101474C","X":1352699521,"Type":"street"}]
			 */

			private String ID;
			private String Name;
			private int Width;
			private int IsCurrent;
			private List<PanosBean> Panos;

			public String getID() {
				return ID;
			}

			public void setID(String ID) {
				this.ID = ID;
			}

			public String getName() {
				return Name;
			}

			public void setName(String Name) {
				this.Name = Name;
			}

			public int getWidth() {
				return Width;
			}

			public void setWidth(int Width) {
				this.Width = Width;
			}

			public int getIsCurrent() {
				return IsCurrent;
			}

			public void setIsCurrent(int IsCurrent) {
				this.IsCurrent = IsCurrent;
			}

			public List<PanosBean> getPanos() {
				return Panos;
			}

			public void setPanos(List<PanosBean> Panos) {
				this.Panos = Panos;
			}

			public static class PanosBean {
				/**
				 * Order : 0
				 * DIR : 161
				 * Y : 364137192
				 * PID : 09000300011606241414065844C
				 * X : 1352697895
				 * Type : street
				 */

				private int Order;
				private int DIR;
				private int Y;
				private String PID;
				private int X;
				private String Type;

				public int getOrder() {
					return Order;
				}

				public void setOrder(int Order) {
					this.Order = Order;
				}

				public int getDIR() {
					return DIR;
				}

				public void setDIR(int DIR) {
					this.DIR = DIR;
				}

				public int getY() {
					return Y;
				}

				public void setY(int Y) {
					this.Y = Y;
				}

				public String getPID() {
					return PID;
				}

				public void setPID(String PID) {
					this.PID = PID;
				}

				public int getX() {
					return X;
				}

				public void setX(int X) {
					this.X = X;
				}

				public String getType() {
					return Type;
				}

				public void setType(String Type) {
					this.Type = Type;
				}
			}
		}

		public static class LinksBean {
			/**
			 * CPointY : 364129890
			 * PID : 09000300011606241414129544C
			 * CPointX : 1352700397
			 * Type : street
			 * RID : aefdd8-46fc-5679-4e04-82c2b7
			 * DIR : 161
			 * Y : 364129174
			 * X : 1352700631
			 */

			private int CPointY;
			private String PID;
			private int CPointX;
			private String Type;
			private String RID;
			private int DIR;
			private int Y;
			private int X;

			public int getCPointY() {
				return CPointY;
			}

			public void setCPointY(int CPointY) {
				this.CPointY = CPointY;
			}

			public String getPID() {
				return PID;
			}

			public void setPID(String PID) {
				this.PID = PID;
			}

			public int getCPointX() {
				return CPointX;
			}

			public void setCPointX(int CPointX) {
				this.CPointX = CPointX;
			}

			public String getType() {
				return Type;
			}

			public void setType(String Type) {
				this.Type = Type;
			}

			public String getRID() {
				return RID;
			}

			public void setRID(String RID) {
				this.RID = RID;
			}

			public int getDIR() {
				return DIR;
			}

			public void setDIR(int DIR) {
				this.DIR = DIR;
			}

			public int getY() {
				return Y;
			}

			public void setY(int Y) {
				this.Y = Y;
			}

			public int getX() {
				return X;
			}

			public void setX(int X) {
				this.X = X;
			}
		}
	}

	@Override
	protected String getTalkingDataPageName() {
		return "小区实景";
	}
}
