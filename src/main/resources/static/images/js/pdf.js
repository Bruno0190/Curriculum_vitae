const puppeteer = require('puppeteer');

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
})();