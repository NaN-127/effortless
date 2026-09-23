FROM python:3.12-slim AS builder

WORKDIR /app

# Install uv for fast dependency resolution and installation
COPY --from=ghcr.io/astral-sh/uv:latest /uv /uvx /bin/

# Copy dependency definition
COPY pyproject.toml .

# Install dependencies into a virtual environment
RUN uv venv /opt/venv && \
    VIRTUAL_ENV=/opt/venv uv pip install .

# Production runner stage
FROM python:3.12-slim AS runner

WORKDIR /app

# Create non-root user
RUN groupadd -r typeless && useradd -r -g typeless -d /app -s /sbin/nologin typeless

# Copy virtualenv from builder
COPY --from=builder /opt/venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

# Copy application source
COPY app/ app/

# Set ownership
RUN chown -R typeless:typeless /app

USER typeless

EXPOSE 8000

ENV APP_ENV=production \
    APP_DEBUG=false \
    PYTHONUNBUFFERED=1

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
