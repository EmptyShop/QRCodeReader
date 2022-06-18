# QRCodeReader
QR Code Reader (ZXing &amp; Code Scanner)

Este proyecto lo hice para comparar 2 librerías de lectura de códigos QR: ZXing y CodeScanner (que está basada en Zxing). La app contiene 2 botones, uno para utilizar cada librería. También tiene 2 controles de texto, uno para desplegar el texto decodificado y el otro para mostrar el formato del código leído.

# Cómo lo Hice

## En este proyecto utilicé:
  - el sdk 32 como target y el sdk 26 como versión mínima.
  - Kotlin versión 1.4.32.
  - ZXing 4.1.0
  - Code Scanner 2.3.2
  - Gradle 4.1.3

## Lo implementé así:
  - En las dependencias agregué la implementación de ZXing: `com.journeyapps:zxing-android-embedded:4.1.0`
  - También añadí la implementación de Code Scanner: `com.github.yuriy-budiyev:code-scanner:2.3.2`
  - Además agregué en la sección de repositorios del archivo build.gradle de proyecto la siguiente referencia:
  
     ```sh
     maven { url 'https://jitpack.io' }
     ```
  
    Esto es para poder usar repositorios alojados en Github y Code Scanner está alojado ahí.
  - Luego, en el manifest, le asigné permisos para usar la cámara:
  
    ```sh
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-feature
        android:name="android.hardware.camera"
        android:required="false" />
    ```
    
  - Para usar Code Scanner usamos un View usado cuando la cámara está capturando el código:

    ```sh
    <com.budiyev.android.codescanner.CodeScannerView
            android:id="@+id/scanner_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
    ```
    
  - En la implementación de la clase MainActivity usamos esta referencia para ZXing:
    
    ```sh
    import com.google.zxing.integration.android.IntentIntegrator
    ```
    
  - Con Code Scanner incializamos algunas cosas:
  
    ```sh
    val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

    codeScanner = CodeScanner(this, scannerView)

    // Parameters (default values)
    codeScanner.camera = CodeScanner.CAMERA_BACK
    codeScanner.formats = CodeScanner.ALL_FORMATS

    codeScanner.autoFocusMode = AutoFocusMode.SAFE
    codeScanner.scanMode = ScanMode.SINGLE
    codeScanner.isAutoFocusEnabled = true
    codeScanner.isFlashEnabled = false

    scannerView.isVisible = false
    ```
        
  - Agregamos callbacks de decodificación y error para Code Scanner:

    ```sh
    // Callbacks
    codeScanner.decodeCallback = DecodeCallback {
        runOnUiThread{
            scannerView.isVisible = false
            messageText.text = it.text
            messageFormat.text = it.barcodeFormat.toString()
        }
    }

    codeScanner.errorCallback = ErrorCallback {
        runOnUiThread{
            Toast.makeText(this, "Camera Initialization error: ${it.message}",
                Toast.LENGTH_LONG).show()
            messageText.text = String()
            messageFormat.text = String()
            scannerView.isVisible = false
        }
    }
    ```
    
    En el código, `messageText` es el TextView para obtener el texto del código QR; `messageFormat` es el Text View para obtener el formato del código.

  - En el caso de ZXing, hice la implementación de su funcionalidad en el evento `onClick` del botón `scanBtnZXing`:

    ```sh
    // adding listener to the button for ZXing
    scanBtnZXing.setOnClickListener(this)

    override fun onClick(v: View?) {
        // we need to create the object
        // of IntentIntegrator class
        // which is the class of ZXing QR library
        val intentIntegrator = IntentIntegrator(this)
        intentIntegrator.setPrompt("Scan a barcode or QR Code")
        intentIntegrator.setOrientationLocked(true)
        intentIntegrator.initiateScan()
    }
    ```

    Eso anterior fue para iniciar la captura del código. Para procesar el resultado lo hice en este método:
    
    ```sh
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null){
            if (intentResult.contents.isNullOrEmpty()){
                Toast.makeText(baseContext, "Cancelled", Toast.LENGTH_SHORT).show()
            }
            else{
                // if the intentResult is not null we'll set
                // the content and format of scan message
                messageText.text = intentResult.contents
                messageFormat.text = intentResult.formatName
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    ```
     
    Aquí uso el intent que creé en el evento `onClick`. Tomo los datos devueltos usando el método `parseActivityResult` y los asigno a los TextViews de formato y texto.
    
## La ayuda que utilicé:
Para este proyecto encontré en internet los siguientes recursos que me parecieron los más confiables y útiles:

  * [¿Cómo leer el código QR usando la biblioteca ZXing en Android?](https://es.acervolima.com/como-leer-el-codigo-qr-usando-la-biblioteca-zxing-en-android/)
  
