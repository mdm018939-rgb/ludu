import React, { Component, ErrorInfo, ReactNode } from 'react';
import { createRoot } from 'react-dom/client';
import App from './App';

const BUILD_VERSION = "build-" + new Date().toISOString();

// Create standalone debug log system
const logs: string[] = [];
let dbgPanel: HTMLDivElement | null = null;
let dbgLogContent: HTMLDivElement | null = null;
let isPanelVisible = false;

function addLog(type: string, message: string) {
  const time = new Date().toLocaleTimeString();
  const entry = `[${time}] [${type}] ${message}`;
  logs.push(entry);

  if (dbgLogContent) {
    const item = document.createElement('div');
    item.textContent = entry;
    if (type === 'ERROR' || type === 'REJECTION') {
      item.style.color = '#ff5555';
    } else if (type === 'WARN') {
      item.style.color = '#ffff55';
    }
    dbgLogContent.appendChild(item);
    dbgLogContent.scrollTop = dbgLogContent.scrollHeight;
  }
}

// Override console methods
const origLog = console.log;
const origWarn = console.warn;
const origError = console.error;

console.log = function (...args: any[]) {
  origLog.apply(console, args);
  addLog('LOG', args.map(a => (typeof a === 'object' ? JSON.stringify(a) : String(a))).join(' '));
};

console.warn = function (...args: any[]) {
  origWarn.apply(console, args);
  addLog('WARN', args.map(a => (typeof a === 'object' ? JSON.stringify(a) : String(a))).join(' '));
};

console.error = function (...args: any[]) {
  origError.apply(console, args);
  addLog('ERROR', args.map(a => (typeof a === 'object' ? JSON.stringify(a) : String(a))).join(' '));
};

// Global Error Listeners
window.onerror = function (msg, url, lineNo, columnNo, error) {
  const errMsg = `${msg} at ${url}:${lineNo}:${columnNo}\n${error && error.stack ? error.stack : ''}`;
  addLog('ERROR', errMsg);
  return false;
};

window.addEventListener('unhandledrejection', function (event) {
  const reason = event.reason ? (event.reason.stack || String(event.reason)) : 'Unknown rejection';
  addLog('REJECTION', reason);
});

// Setup DOM Debug elements (mounted outside React tree)
function initDebugDOM() {
  try {
    // Floating DBG button
    const dbgBtn = document.createElement('button');
    dbgBtn.id = 'dbg-floating-btn';
    dbgBtn.textContent = 'DBG';
    dbgBtn.style.cssText = `
      position: fixed;
      top: 10px;
      right: 10px;
      z-index: 99999;
      background: rgba(255, 215, 0, 0.85);
      color: #071330;
      border: 2px solid #071330;
      border-radius: 12px;
      padding: 6px 12px;
      font-family: monospace;
      font-weight: bold;
      font-size: 13px;
      cursor: pointer;
      box-shadow: 0 4px 8px rgba(0,0,0,0.5);
    `;

    // Full-width Debug Panel
    dbgPanel = document.createElement('div');
    dbgPanel.id = 'dbg-panel';
    dbgPanel.style.cssText = `
      display: none;
      position: fixed;
      top: 0;
      left: 0;
      width: 100vw;
      max-height: 40vh;
      z-index: 99998;
      background: #000000;
      color: #00ff00;
      font-family: monospace;
      font-size: 11px;
      box-sizing: border-box;
      border-bottom: 3px solid #ffd700;
      overflow-y: auto;
      padding: 10px;
    `;

    const infoHeader = document.createElement('div');
    infoHeader.style.cssText = 'border-bottom: 1px solid #333; padding-bottom: 6px; margin-bottom: 6px; color: #00e5ff;';
    infoHeader.innerHTML = `
      <div><strong>App Version:</strong> ${BUILD_VERSION}</div>
      <div><strong>Viewport:</strong> ${window.innerWidth} x ${window.innerHeight}</div>
      <div><strong>UserAgent:</strong> ${navigator.userAgent}</div>
    `;

    dbgLogContent = document.createElement('div');
    dbgLogContent.style.cssText = 'white-space: pre-wrap; word-break: break-all;';

    // Populate existing logs
    logs.forEach(logStr => {
      const item = document.createElement('div');
      item.textContent = logStr;
      dbgLogContent?.appendChild(item);
    });

    dbgPanel.appendChild(infoHeader);
    dbgPanel.appendChild(dbgLogContent);

    dbgBtn.addEventListener('click', () => {
      isPanelVisible = !isPanelVisible;
      if (dbgPanel) {
        dbgPanel.style.display = isPanelVisible ? 'block' : 'none';
      }
    });

    document.body.appendChild(dbgPanel);
    document.body.appendChild(dbgBtn);
  } catch (e) {
    origError('Failed to initialize Debug DOM:', e);
  }
}

// React Error Boundary
interface Props {
  children: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
  errorInfo: ErrorInfo | null;
}

class ErrorBoundary extends Component<Props, State> {
  public state: State = {
    hasError: false,
    error: null,
    errorInfo: null
  };

  public static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error, errorInfo: null };
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    this.setState({ error, errorInfo });
    addLog('REACT_ERROR', `${error.toString()}\n${errorInfo.componentStack}`);
  }

  public render() {
    if (this.state.hasError) {
      return (
        <div style={{
          padding: '20px',
          color: '#ffaaaa',
          backgroundColor: '#220000',
          height: '100vh',
          boxSizing: 'border-box',
          fontFamily: 'monospace',
          overflow: 'auto'
        }}>
          <h2>React Error Boundary Caught An Exception</h2>
          <pre>{this.state.error && this.state.error.toString()}</pre>
          <pre>{this.state.errorInfo && this.state.errorInfo.componentStack}</pre>
        </div>
      );
    }
    return this.props.children;
  }
}

// Initialize Debugger and render App
initDebugDOM();
console.log("app start");

try {
  const container = document.getElementById('root');
  if (container) {
    const root = createRoot(container);
    root.render(
      <React.StrictMode>
        <ErrorBoundary>
          <App />
        </ErrorBoundary>
      </React.StrictMode>
    );
  }
} catch (e: any) {
  addLog('FATAL_MOUNT', e && e.stack ? e.stack : String(e));
}
