import React, { useState, useEffect } from 'react';

// Safe Audio Helper
function playToneSound(freq: number, durationMs: number) {
  try {
    const AudioCtx = window.AudioContext || (window as any).webkitAudioContext;
    if (!AudioCtx) return;
    const ctx = new AudioCtx();
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();
    osc.type = 'sine';
    osc.frequency.setValueAtTime(freq, ctx.currentTime);
    gain.gain.setValueAtTime(0.1, ctx.currentTime);
    osc.connect(gain);
    gain.connect(ctx.destination);
    osc.start();
    osc.stop(ctx.currentTime + durationMs / 1000);
  } catch (e) {
    // Ignore audio errors silently
  }
}

// Safe Storage Helper
function safeGetStorage(key: string): any {
  try {
    const item = localStorage.getItem(key);
    return item ? JSON.parse(item) : null;
  } catch (e) {
    console.warn('Storage read failed:', e);
    return null;
  }
}

function safeSetStorage(key: string, value: any) {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch (e) {
    console.warn('Storage write failed:', e);
  }
}

type Screen = 'splash' | 'home' | 'selectGame' | 'chooseColor' | 'game' | 'winner';

export default function App() {
  const [screen, setScreenState] = useState<Screen>('splash');
  const [selectedMode, setSelectedMode] = useState<'classic' | 'quick'>('classic');
  const [playerCount, setPlayerCount] = useState<number>(2);
  const [pairOption, setPairOption] = useState<'rowA' | 'rowB'>('rowB');
  const [playerNames, setPlayerNames] = useState<{ [key: string]: string }>({
    RED: 'Player 1',
    YELLOW: 'Player 2',
    GREEN: 'Player 3',
    BLUE: 'Player 4'
  });
  const [oneTokenOut, setOneTokenOut] = useState<boolean>(false);

  const changeScreen = (newScreen: Screen) => {
    console.log(`screen changed to ${newScreen}`);
    setScreenState(newScreen);
  };

  // 1. SPLASH SCREEN FIX
  useEffect(() => {
    if (screen === 'splash') {
      console.log('Splash mounted');
      const timer = setTimeout(() => {
        console.log('Splash timer fired');
        changeScreen('home');
      }, 1500);
      return () => clearTimeout(timer);
    } else if (screen === 'home') {
      console.log('Home mounted');
    } else if (screen === 'game') {
      console.log('Board mounted');
    }
  }, [screen]);

  // Splash Screen Render
  if (screen === 'splash') {
    return (
      <div
        onClick={() => {
          console.log('Splash clicked manually');
          changeScreen('home');
        }}
        style={{
          width: '100vw',
          height: '100vh',
          backgroundColor: '#071330',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#ffffff',
          fontFamily: 'sans-serif',
          cursor: 'pointer',
          userSelect: 'none'
        }}
      >
        <h1 style={{ fontSize: '3rem', color: '#ffd700', margin: '0 0 10px 0' }}>M LUDO</h1>
        <p style={{ fontSize: '1.2rem', color: '#aaaaaa', margin: '0 0 30px 0' }}>Pass N Play Classic Game</p>
        <div style={{
          width: '180px',
          height: '8px',
          backgroundColor: 'rgba(255,255,255,0.2)',
          borderRadius: '4px',
          overflow: 'hidden'
        }}>
          <div style={{
            width: '100%',
            height: '100%',
            backgroundColor: '#ffd700',
            animation: 'pulse 1.5s infinite'
          }}></div>
        </div>
      </div>
    );
  }

  // Home Screen Render
  if (screen === 'home') {
    return (
      <div style={{
        width: '100vw',
        height: '100vh',
        backgroundColor: '#071330',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'space-between',
        color: '#ffffff',
        fontFamily: 'sans-serif',
        padding: '20px',
        boxSizing: 'border-box'
      }}>
        <div style={{ width: '100%', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div style={{ width: '40px', height: '40px', borderRadius: '50%', backgroundColor: '#0072ff', border: '2px solid #ffd700' }}></div>
            <div>
              <div style={{ fontWeight: 'bold' }}>Player 1</div>
              <div style={{ fontSize: '0.8rem', color: '#ffd700' }}>Level 2</div>
            </div>
          </div>
          <div style={{ background: '#062b6d', border: '1.5px solid #ffd700', padding: '6px 14px', borderRadius: '16px', fontWeight: 'bold' }}>
            🪙 1,550
          </div>
        </div>

        <div style={{ textAlign: 'center' }}>
          <h1 style={{ fontSize: '3.5rem', color: '#ffd700', margin: 0 }}>M LUDO</h1>
          <p style={{ color: '#888' }}>Pass N Play Local Match</p>
          <button
            onClick={() => {
              console.log('Pass N Play button clicked');
              playToneSound(600, 50);
              changeScreen('selectGame');
            }}
            style={{
              marginTop: '30px',
              padding: '16px 40px',
              fontSize: '1.5rem',
              fontWeight: 'bold',
              color: '#071330',
              background: 'linear-gradient(to bottom, #ffe052, #e5a100)',
              border: '3px solid #fff5a3',
              borderRadius: '28px',
              boxShadow: '0 8px 16px rgba(0,0,0,0.4)',
              cursor: 'pointer'
            }}
          >
            PASS N PLAY
          </button>
        </div>

        <div style={{ width: '100%', textAlign: 'center', color: '#666', fontSize: '0.9rem' }}>
          M Ludo v1.0 • Offline Pass N Play
        </div>
      </div>
    );
  }

  // Select Game Screen
  if (screen === 'selectGame') {
    return (
      <div style={{
        width: '100vw',
        height: '100vh',
        backgroundColor: '#071330',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '20px',
        boxSizing: 'border-box'
      }}>
        <div style={{
          width: '100%',
          maxWidth: '400px',
          background: 'linear-gradient(to bottom, #0a2b70, #04102c)',
          border: '2px solid #ffd700',
          borderRadius: '24px',
          padding: '20px',
          color: '#fff',
          boxSizing: 'border-box'
        }}>
          <h2 style={{ textAlign: 'center', color: '#ffd700', marginTop: 0 }}>SELECT GAME</h2>
          <div
            onClick={() => setSelectedMode('classic')}
            style={{
              padding: '16px',
              borderRadius: '16px',
              background: selectedMode === 'classic' ? '#0052b3' : '#081c45',
              border: selectedMode === 'classic' ? '2.5px solid #ffd700' : '1px solid #444',
              marginBottom: '12px',
              cursor: 'pointer'
            }}
          >
            <div style={{ fontWeight: 'bold', fontSize: '1.2rem' }}>Classic</div>
            <div style={{ fontSize: '0.9rem', color: '#ccc' }}>Standard rules, bring 4 tokens home</div>
          </div>

          <div
            onClick={() => setSelectedMode('quick')}
            style={{
              padding: '16px',
              borderRadius: '16px',
              background: selectedMode === 'quick' ? '#0052b3' : '#081c45',
              border: selectedMode === 'quick' ? '2.5px solid #ffd700' : '1px solid #444',
              marginBottom: '24px',
              cursor: 'pointer'
            }}
          >
            <div style={{ fontWeight: 'bold', fontSize: '1.2rem' }}>Quick</div>
            <div style={{ fontSize: '0.9rem', color: '#ccc' }}>Faster gameplay</div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <button
              onClick={() => changeScreen('home')}
              style={{ padding: '12px 20px', borderRadius: '20px', background: '#333', color: '#fff', border: 'none' }}
            >
              Back
            </button>
            <button
              onClick={() => {
                console.log('Next button clicked');
                playToneSound(600, 50);
                changeScreen('chooseColor');
              }}
              style={{
                padding: '12px 36px',
                fontSize: '1.2rem',
                fontWeight: 'bold',
                color: '#071330',
                background: 'linear-gradient(to bottom, #ffe052, #e5a100)',
                border: 'none',
                borderRadius: '20px',
                cursor: 'pointer'
              }}
            >
              Next
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Choose Color Screen
  if (screen === 'chooseColor') {
    return (
      <div style={{
        width: '100vw',
        height: '100vh',
        backgroundColor: '#071330',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '20px',
        boxSizing: 'border-box'
      }}>
        <div style={{
          width: '100%',
          maxWidth: '400px',
          background: 'linear-gradient(to bottom, #0a2b70, #04102c)',
          border: '2px solid #ffd700',
          borderRadius: '24px',
          padding: '20px',
          color: '#fff',
          boxSizing: 'border-box'
        }}>
          <h2 style={{ textAlign: 'center', color: '#ffd700', marginTop: 0 }}>CHOOSE COLOR AND NAME</h2>

          <div style={{ display: 'flex', justifyContent: 'space-around', marginBottom: '20px' }}>
            {[2, 3, 4].map(num => (
              <button
                key={num}
                onClick={() => setPlayerCount(num)}
                style={{
                  padding: '8px 20px',
                  borderRadius: '12px',
                  fontWeight: 'bold',
                  background: playerCount === num ? '#0072ff' : '#061538',
                  color: playerCount === num ? '#ffd700' : '#fff',
                  border: playerCount === num ? '2px solid #ffd700' : '1px solid #444'
                }}
              >
                {num}P
              </button>
            ))}
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', marginBottom: '24px' }}>
            <button
              onClick={() => {
                console.log('Play button clicked');
                playToneSound(700, 60);
                setOneTokenOut(false);
                changeScreen('game');
              }}
              style={{
                padding: '14px',
                fontSize: '1.2rem',
                fontWeight: 'bold',
                color: '#071330',
                background: 'linear-gradient(to bottom, #ffe052, #e5a100)',
                border: 'none',
                borderRadius: '20px',
                cursor: 'pointer'
              }}
            >
              Play (Need 6 for token out)
            </button>

            <button
              onClick={() => {
                console.log('Play One Token Out button clicked');
                playToneSound(700, 60);
                setOneTokenOut(true);
                changeScreen('game');
              }}
              style={{
                padding: '14px',
                fontSize: '1.1rem',
                fontWeight: 'bold',
                color: '#071330',
                background: 'linear-gradient(to bottom, #ffe052, #e5a100)',
                border: 'none',
                borderRadius: '20px',
                cursor: 'pointer'
              }}
            >
              Play - One Token Out -
            </button>
          </div>

          <button
            onClick={() => changeScreen('selectGame')}
            style={{ padding: '10px 20px', borderRadius: '16px', background: '#333', color: '#fff', border: 'none' }}
          >
            Back
          </button>
        </div>
      </div>
    );
  }

  // Game Screen
  if (screen === 'game') {
    return (
      <div style={{
        width: '100vw',
        height: '100vh',
        backgroundColor: '#071330',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '10px',
        boxSizing: 'border-box'
      }}>
        <div style={{ color: '#ffd700', fontWeight: 'bold' }}>Player 2 (Yellow)</div>

        <div style={{
          width: '90vw',
          height: '90vw',
          maxWidth: '400px',
          maxHeight: '400px',
          backgroundColor: '#ffffff',
          border: '4px solid #071330',
          borderRadius: '16px',
          display: 'grid',
          gridTemplateColumns: 'repeat(15, 1fr)',
          gridTemplateRows: 'repeat(15, 1fr)',
          position: 'relative'
        }}>
          {/* Ludo Board representation */}
          <div style={{ gridColumn: '1 / 7', gridRow: '1 / 7', backgroundColor: '#43a047' }}></div>
          <div style={{ gridColumn: '10 / 16', gridRow: '1 / 7', backgroundColor: '#fdd835' }}></div>
          <div style={{ gridColumn: '1 / 7', gridRow: '10 / 16', backgroundColor: '#e53935' }}></div>
          <div style={{ gridColumn: '10 / 16', gridRow: '10 / 16', backgroundColor: '#1e88e5' }}></div>
        </div>

        <div style={{ width: '100%', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <button
            onClick={() => changeScreen('home')}
            style={{ padding: '10px 20px', borderRadius: '12px', background: '#333', color: '#fff', border: 'none' }}
          >
            Menu
          </button>

          <button
            onClick={() => {
              const val = Math.floor(Math.random() * 6) + 1;
              console.log(`dice rolled with value ${val}`);
              playToneSound(500, 40);
              console.log(`token moved with from 0 to ${val}`);
            }}
            style={{
              padding: '16px 28px',
              fontSize: '1.3rem',
              fontWeight: 'bold',
              background: '#ffffff',
              color: '#000000',
              border: '3px solid #ffd700',
              borderRadius: '16px',
              cursor: 'pointer'
            }}
          >
            🎲 Roll
          </button>
        </div>
      </div>
    );
  }

  return null;
}
