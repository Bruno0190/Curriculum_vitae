const puppeteer = require('puppeteer');

(async () => {
  const curriculumId = process.argv[2];

  if (!curriculumId) {
    console.error('Errore: ID curriculum mancante!');
    process.exit(1);
  }

  const rawBaseUrl = process.env.PDF_BASE_URL || process.env.RENDER_EXTERNAL_URL || 'http://localhost:8080';
  const baseUrl = rawBaseUrl.replace(/\/$/, '');

  let browser;

  try {
    browser = await puppeteer.launch({
      headless: true,
      args: ['--no-sandbox', '--disable-setuid-sandbox', '--disable-dev-shm-usage']
    });

    const page = await browser.newPage();
    await page.emulateMediaType('print');

    await page.goto(`${baseUrl}/curriculums/show/${curriculumId}`, {
      waitUntil: 'domcontentloaded',
      timeout: 60000
    });

    await page.waitForNetworkIdle({ idleTime: 500, timeout: 60000 });
    await page.waitForSelector('#container_cv', { timeout: 10000 });
    await page.evaluate(async () => {
      if (document.fonts && document.fonts.ready) {
        await document.fonts.ready;
      }
    });

    const pdfBuffer = await page.pdf({
      format: 'A4',
      printBackground: true,
      preferCSSPageSize: true,
      tagged: true,
      margin: { top: '10mm', right: '10mm', bottom: '10mm', left: '10mm' }
    });

    process.stdout.write(pdfBuffer);
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