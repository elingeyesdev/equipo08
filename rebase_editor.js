const fs = require('fs');
const file = process.argv[2];
let content = fs.readFileSync(file, 'utf8');

// For sequence editor: replace the first 'pick' with 'reword'
if (file.includes('git-rebase-todo')) {
  content = content.replace(/^pick/, 'reword');
  fs.writeFileSync(file, content);
} 
// For commit editor: replace the commit message
else if (file.includes('COMMIT_EDITMSG')) {
  const lines = content.split('\n');
  lines[0] = 'E11. Rediseño de interfaz premium';
  fs.writeFileSync(file, lines.join('\n'));
}
