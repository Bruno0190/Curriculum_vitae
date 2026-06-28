const puppeteer = require('puppeteer');

(async () => {
  // 1. Recuperiamo l'ID che gli passeremo da riga di comando
  const curriculumId = process.argv[2];
  
  if (!curriculumId) {
    console.error("Errore: ID curriculum mancante!");
    process.exit(1);
  }

  const browser = await puppeteer.launch({
    headless: true,
    args: ['--no-sandbox', '--disable-setuid-sandbox'] 
  });

  const page = await browser.newPage();

  // 2. Controllo URL: se siamo su Render usa la sua variabile, altrimenti localhost
  const baseUrl = process.env.RENDER_EXTERNAL_URL || 'http://localhost:8080';

  // 3. Naviga alla pagina esatta del CV usando l'ID
  await page.goto(`${baseUrl}/curriculums/show/${curriculumId}`, {
    waitUntil: 'networkidle0'
  });

  // 4. Genera il PDF in memoria
  const pdfBuffer = await page.pdf({
    format: 'A4',
    printBackground: true, 
    margin: { top: '10mm', right: '10mm', bottom: '10mm', left: '10mm' } 
  });

  await browser.close();

  // 5. Spedisce i byte del PDF direttamente sulla console (stdout)
  // Questo serve a Java per "intercettare" il file senza salvarlo sul disco fisso
  process.stdout.write(pdfBuffer);
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