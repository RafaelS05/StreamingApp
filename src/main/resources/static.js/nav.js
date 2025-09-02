        // Efecto de scroll en navbar
        window.addEventListener('scroll', function() {
            const navbar = document.querySelector('.navbar');
            if (window.scrollY > 100) {
                navbar.style.background = 'linear-gradient(90deg, rgba(30, 144, 255, 0.95), rgba(138, 43, 226, 0.95))';
                navbar.style.backdropFilter = 'blur(10px)';
            } else {
                navbar.style.background = 'linear-gradient(90deg, #1E90FF, #8A2BE2)';
                navbar.style.backdropFilter = 'none';
            }
        });

        // Smooth scrolling para los enlaces de navegación
        document.querySelectorAll('.navbar a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                const target = document.querySelector(this.getAttribute('href'));
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            });
        });

        // Efecto de hover mejorado para las cards
        document.querySelectorAll('.content-card').forEach(card => {
            card.addEventListener('mouseenter', function() {
                this.style.transform = 'scale(1.05) rotateY(5deg)';
                this.style.boxShadow = '0px 15px 40px rgba(30, 144, 255, 0.6)';
            });
            
            card.addEventListener('mouseleave', function() {
                this.style.transform = 'scale(1) rotateY(0deg)';
                this.style.boxShadow = '0px 0px 20px rgba(30, 144, 255, 0.2)';
            });
        });

        // Funcionalidad para los botones del hero
        document.querySelector('.btn-primary').addEventListener('click', function() {
            alert('🎬 ¡Iniciando reproducción! Funcionalidad próximamente...');
        });

        document.querySelector('.btn-secondary').addEventListener('click', function() {
            alert('ℹ️ Más información sobre este contenido próximamente...');
        });

        // Efecto de scroll horizontal suave para los sliders
        document.querySelectorAll('.content-slider').forEach(slider => {
            let isDown = false;
            let startX;
            let scrollLeft;

            slider.addEventListener('mousedown', (e) => {
                isDown = true;
                startX = e.pageX - slider.offsetLeft;
                scrollLeft = slider.scrollLeft;
                slider.style.cursor = 'grabbing';
            });

            slider.addEventListener('mouseleave', () => {
                isDown = false;
                slider.style.cursor = 'grab';
            });

            slider.addEventListener('mouseup', () => {
                isDown = false;
                slider.style.cursor = 'grab';
            });

            slider.addEventListener('mousemove', (e) => {
                if (!isDown) return;
                e.preventDefault();
                const x = e.pageX - slider.offsetLeft;
                const walk = (x - startX) * 2;
                slider.scrollLeft = scrollLeft - walk;
            });
        });