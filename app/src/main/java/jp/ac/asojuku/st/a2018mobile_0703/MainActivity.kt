package jp.ac.asojuku.st.a2018mobile_0703

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , SensorEventListener, SurfaceHolder.Callback{

    override fun surfaceCreated(holder: SurfaceHolder?) {
        val sensorManager=getSystemService(Context.SENSOR_SERVICE)
                as SensorManager
        val accSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorManager.registerListener(this,accSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        val sensorManager=getSystemService(Context.SENSOR_SERVICE)
                as SensorManager
        sensorManager.unregisterListener(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

        surfaceWidth=width
        surfaceHeight=height

        ballX=(width/2).toFloat()
        ballY=(height/2).toFloat()
    }

    //サーフェスビューの幅
    private var surfaceWidth: Int=0
    //サーフェスビューの高さ
    private var surfaceHeight: Int=0

    //ボールの半径を表す定義
    private val radius =50.0f
    //ボールの移動量を調整するための計数
    private val coef=300.0f
    //ボールの現在のX座標
    private var ballX: Float = 0f
    //ボールの現在のY座標
    private var ballY: Float = 0f
    //ボールのX方向への加速度
    private var vx: Float = 0f
    //ボールのY方向への加速度
    private var vy: Float = 0f
    //前回時間の保持
    private var time: Long = 0L
    private var count = 1


    //センサーの値が変わった時のイベントコールバック
    override fun onSensorChanged(event: SensorEvent?) {

        //イベントが何もなかったらそのままリターン
        if(event==null) return

        if(time==0L) time = System.currentTimeMillis()
        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER){
            val x= -event.values[0]
            val y= event.values[1]

            var t=(System.currentTimeMillis() - time).toFloat()
            time=System.currentTimeMillis()
            t /= 1000.0f

            val dx= vx * t + x * t * t/2.0f
            val dy= vy * t + y * t * t/2.0f
            ballX+=dx*coef
            ballY+=dy*coef
            vx+=x*t
            vy+=y*t


            if (301f < ballY + radius  && ballY - radius <451f  && ballX - radius < 801f ){
                val sensorManager=this.getSystemService(Context.SENSOR_SERVICE)
                        as SensorManager
                sensorManager.unregisterListener(this)
                imageView1.setImageResource(R.drawable.nc21429)
            }

            if (0f < ballY + radius  && ballY - radius <301f  && ballX - radius < 101f ){
                val sensorManager=this.getSystemService(Context.SENSOR_SERVICE)
                        as SensorManager
                sensorManager.unregisterListener(this)
                imageView2.setImageResource(R.drawable.ncd)
            }

            if (301f < ballY + radius  && ballY - radius <1101f && 801 < ballX+radius && ballX - radius < 901f ){
                val sensorManager=this.getSystemService(Context.SENSOR_SERVICE)
                        as SensorManager
                sensorManager.unregisterListener(this)
                imageView1.setImageResource(R.drawable.nc21429)
            }

            if (801f < ballY + radius  && ballY - radius <1101f && 201 < ballX+radius && ballX - radius < 901f ){
                val sensorManager=this.getSystemService(Context.SENSOR_SERVICE)
                        as SensorManager
                sensorManager.unregisterListener(this)
                imageView1.setImageResource(R.drawable.nc21429)
            }

            //左
            if (ballX - radius < 0 && vx < 0){
                vx = -vx*2f
                ballX=radius
            }
            //右
            else if(ballX+radius>surfaceWidth && vx > 0){
                vx = -vx *2f
                ballX=surfaceWidth-radius
            }
            //上
            if (ballY - radius <0 && vy < 0){
                vy = -vy *2f
                ballY=radius
            }else if(ballY+radius>surfaceHeight && vy > 0){
                vy = -vy *2f
                ballY=surfaceHeight-radius
            }

            drawCanvas()
        }



        //センサーの値が変わったらログに出力
        //加速度センサーが判定
        if(event.sensor.type== Sensor.TYPE_ACCELEROMETER){
            Log.d("MainActivity",
                "x=${event.values[0].toString()}"+
                        ",y=${event.values[1].toString()}"+
                        ",z=${event.values[2].toString()}")
        }
    }
    //制度が変わった時のイベントコールバック
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    //画面表示・再表示のライフサイクルイベント
    override fun onResume() {
        //親クラスのonResume()処理
        super.onResume()
        //自分クラスのonResume()処理
        //センサーマネージャーをOSから取得
        val sensorManager=getSystemService(Context.SENSOR_SERVICE)
                as SensorManager
        sensorManager.unregisterListener(this)

        reset.setOnClickListener{
            finish()
            startActivity(intent)
        }
    }


    override fun onPause() {
        super.onPause()
        //センサーマネージャーを取得
        val sensorManager=this.getSystemService(Context.SENSOR_SERVICE)
                as SensorManager
        sensorManager.unregisterListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //画面が回転しないようにする
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContentView(R.layout.activity_main)
        val holder=surfaceView.holder
        holder.addCallback(this)
    }

    //ボール位置をサーフェイスビューに描画する
    private fun drawCanvas(){
        val canvas = surfaceView.holder.lockCanvas()
        canvas.drawColor(Color.rgb(160, 200, 255))
        canvas.drawCircle(ballX,ballY,radius, Paint().apply {
            color = Color.rgb(255, 70, 0)
        })
        canvas.drawRect(5f, 300f, 800f, 450f, Paint().apply {
            color = Color.BLACK
        })
        canvas.drawRect(800f, 300f, 900f, 1100f, Paint().apply {
            color = Color.BLACK
        })
        canvas.drawRect(200f, 800f, 900f, 1100f, Paint().apply {
            color = Color.BLACK
        })
        canvas.drawRect(5f, 0f, 100f, 300f, Paint().apply {
            color = Color.GREEN
        })
        surfaceView.holder.unlockCanvasAndPost(canvas)
    }
}
