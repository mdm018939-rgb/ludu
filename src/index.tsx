import React, { Component, ErrorInfo, ReactNode } from 'react';
import { createRoot } from 'react-dom/client';

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
    console.error('Uncaught React Error:', error, errorInfo);
  }

  public render() {
    if (this.state.hasError) {
      return (
        <div style={{
          padding: '20px',
          color: '#ffaaaa',
          backgroundColor: '#330000',
          height: '100vh',
          boxSizing: 'border-box',
          fontFamily: 'monospace',
          overflow: 'auto'
        }}>
          <h2>App Runtime Error</h2>
          <pre>{this.state.error && this.state.error.toString()}</pre>
          <pre>{this.state.errorInfo && this.state.errorInfo.componentStack}</pre>
        </div>
      );
    }
    return this.props.children;
  }
}

function App() {
  return (
    <div style={{
      width: '100%',
      height: '100%',
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center',
      alignItems: 'center',
      color: '#ffd700',
      backgroundColor: '#071330',
      textAlign: 'center',
      padding: '20px',
      boxSizing: 'border-box'
    }}>
      <h1 style={{ fontSize: '2.5rem', marginBottom: '10px' }}>M LUDO</h1>
      <p style={{ fontSize: '1.2rem', color: '#ffffff' }}>Pass N Play Classic Game</p>
    </div>
  );
}

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
  const errDiv = document.getElementById('error-display');
  if (errDiv) {
    errDiv.style.display = 'block';
    errDiv.innerText += '\n[MOUNT ERROR] ' + (e && e.stack ? e.stack : e);
  }
}
