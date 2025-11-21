const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');

// State
let ballPosition = { x: 0, y: 0 };
let dragStart = { x: 0, y: 0 };
let isDraggingBall = false;
let isAiming = false;
let aimVector = { x: 0, y: 0 };

// Constants
const BALL_RADIUS = 30; // Scaled for web
const LINE_WIDTH = 3;
const MAX_STEPS = 2000;
const STEP_SIZE = 10;

// Resize handling
function resize() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    // Reset ball if off screen or init
    if (ballPosition.x === 0 && ballPosition.y === 0) {
        ballPosition.x = canvas.width / 2;
        ballPosition.y = canvas.height / 2;
    }
}
window.addEventListener('resize', resize);
resize();

// Input handling
canvas.addEventListener('pointerdown', onPointerDown);
canvas.addEventListener('pointermove', onPointerMove);
canvas.addEventListener('pointerup', onPointerUp);

function onPointerDown(e) {
    const x = e.clientX;
    const y = e.clientY;

    // Check if touching ball
    const dx = x - ballPosition.x;
    const dy = y - ballPosition.y;
    if (dx * dx + dy * dy <= BALL_RADIUS * BALL_RADIUS) {
        isDraggingBall = true;
    } else {
        isAiming = true;
        dragStart.x = x;
        dragStart.y = y;
        aimVector.x = 0;
        aimVector.y = 0;
    }
    draw();
}

function onPointerMove(e) {
    const x = e.clientX;
    const y = e.clientY;

    if (isDraggingBall) {
        ballPosition.x = x;
        ballPosition.y = y;
    } else if (isAiming) {
        // Slingshot: Vector is Start - Current
        aimVector.x = dragStart.x - x;
        aimVector.y = dragStart.y - y;
    }
    draw();
}

function onPointerUp(e) {
    isDraggingBall = false;
    // Keep aiming true to show the line? Or clear?
    // Native app decision was to keep it? Let's keep it for now.
    // But if we want to "shoot", we might want to clear.
    // For a guide, usually you want to see the line after you lift your finger to confirm.
    // So we won't clear isAiming here.
    // However, if we tap somewhere else, we start a new aim.
    draw();
}

// Drawing
function draw() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Draw Ball
    ctx.beginPath();
    ctx.arc(ballPosition.x, ballPosition.y, BALL_RADIUS, 0, Math.PI * 2);
    ctx.fillStyle = 'rgba(0, 255, 255, 0.6)'; // Cyan with alpha
    ctx.fill();

    // Draw Trajectory
    if (isAiming) {
        drawTrajectory();
    }
}

function drawTrajectory() {
    let currentX = ballPosition.x;
    let currentY = ballPosition.y;

    // Normalize aim vector
    const length = Math.sqrt(aimVector.x * aimVector.x + aimVector.y * aimVector.y);
    if (length === 0) return;

    let dirX = aimVector.x / length;
    let dirY = aimVector.y / length;

    ctx.beginPath();
    ctx.moveTo(currentX, currentY);
    ctx.strokeStyle = 'red';
    ctx.lineWidth = LINE_WIDTH;

    for (let i = 0; i < MAX_STEPS; i++) {
        currentX += dirX * STEP_SIZE;
        currentY += dirY * STEP_SIZE;

        // Reflection logic
        let reflected = false;
        if (currentX <= 0 || currentX >= canvas.width) {
            dirX = -dirX;
            currentX = currentX <= 0 ? 0 : canvas.width;
            reflected = true;
        }
        if (currentY <= 0 || currentY >= canvas.height) {
            dirY = -dirY;
            currentY = currentY <= 0 ? 0 : canvas.height;
            reflected = true;
        }

        ctx.lineTo(currentX, currentY);
    }
    ctx.stroke();
}

// Initial draw
draw();
