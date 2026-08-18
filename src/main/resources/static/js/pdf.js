const puppeteer = require('puppeteer');
const fs = require('fs');

(async () => {
  const curriculumId = process.argv[2];
  const outputPath = process.argv[3];

  if (!curriculumId) {
    console.error('Errore: ID curriculum mancante!');
    process.exit(1);
  }

  if (!outputPath) {
    console.error('Errore: percorso output PDF mancante!');
    process.exit(1);
  }

  const runtimePort = process.env.PORT || '8080';
  const internalBaseUrl = `http://127.0.0.1:${runtimePort}`;
  const externalBaseUrl = process.env.RENDER_EXTERNAL_URL;
  const configuredBaseUrl = process.env.PDF_BASE_URL;

  const candidateBaseUrls = [
    configuredBaseUrl,
    internalBaseUrl,
    externalBaseUrl,
    'http://localhost:8080'
  ]
    .filter(Boolean)
    .map((url) => url.replace(/\/$/, ''))
    .filter((value, index, self) => self.indexOf(value) === index);

  process.stdout.write(`PDF_BASE_URL_CANDIDATES=${candidateBaseUrls.join(',')}`);

  let browser;

  try {
    const launchOptions = {
      headless: true,
      args: ['--no-sandbox', '--disable-setuid-sandbox', '--disable-dev-shm-usage']
    };

    if (process.env.PUPPETEER_EXECUTABLE_PATH) {
      launchOptions.executablePath = process.env.PUPPETEER_EXECUTABLE_PATH;
    }

    browser = await puppeteer.launch(launchOptions);

    const page = await browser.newPage();
    await page.emulateMediaType('print');

    let loaded = false;
    let lastError = null;

    for (const baseUrl of candidateBaseUrls) {
      try {
        const response = await page.goto(`${baseUrl}/curriculums/show/${curriculumId}`, {
          waitUntil: 'domcontentloaded',
          timeout: 60000
        });

        if (!response) {
          throw new Error(`Nessuna risposta HTTP da ${baseUrl}`);
        }

        if (response.status() >= 400) {
          throw new Error(`HTTP ${response.status()} su ${response.url()}`);
        }

        // Some hosts keep background requests open; avoid hard-failing on network idle.
        try {
          await page.waitForNetworkIdle({ idleTime: 500, timeout: 15000 });
        } catch {
          // Continue with selector-based readiness below.
        }
        await page.waitForSelector('#container_cv', { timeout: 10000 });
        await page.evaluate(async () => {
          if (document.fonts && document.fonts.ready) {
            await document.fonts.ready;
          }
        });

        loaded = true;
        break;
      } catch (error) {
        lastError = new Error(`${baseUrl}: ${error.message}`);
      }
    }

    if (!loaded) {
      throw new Error(`Nessun base URL raggiungibile per il rendering (${lastError ? lastError.message : 'errore sconosciuto'})`);
    }

    const pdfBuffer = await page.pdf({
      format: 'A4',
      printBackground: true,
      preferCSSPageSize: true,
      tagged: true,
      margin: { top: '10mm', right: '10mm', bottom: '10mm', left: '10mm' }
    });

    fs.writeFileSync(outputPath, pdfBuffer);
    process.stdout.write('PDF_OK');
  } catch (error) {
    console.error(`Errore generazione PDF: ${error.message}`);
    process.exit(1);
  } finally {
    if (browser) {
      await browser.close();
    }
  }
})();







/* const puppeteer = require('puppeteer');

(async () => {
  const browser = await puppeteer.launch({
    headless: true,
    args: ['--no-sandbox']
  });

  const page = await browser.newPage();

  await page.goto('http://localhost:8080/', {
    waitUntil: 'networkidle0'
  });

  await page.pdf({
    path: 'cv.pdf',
    format: 'A4',
    printBackground: true
  });

  await browser.close();
})(); */